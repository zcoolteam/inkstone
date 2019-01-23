package com.zcool.inkstone.security;

import com.zcool.inkstone.lang.Base64;
import com.zcool.inkstone.lang.Charsets;
import com.zcool.inkstone.lang.Singleton;
import com.zcool.inkstone.util.ContextUtil;

import java.security.SecureRandom;
import java.util.Arrays;

import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import timber.log.Timber;

public class AES {

    private final V1 mV1;

    public AES(@Nullable String key) {
        mV1 = new V1(key);
    }

    public String encode(@Nullable String input, @Nullable String defaultValue) {
        return mV1.encode(input, defaultValue);
    }

    public String decode(@Nullable String input, @Nullable String defaultValue) {
        return mV1.decode(input, defaultValue);
    }

    private static class V1 {

        private static final String VERSION = "V1";
        private final String mKey;
        private final Cipher mEncoder;
        private final Cipher mDecoder;

        private V1(@Nullable String key) {
            String packageName = ContextUtil.getContext().getPackageName();
            mKey = key + ";" + packageName;

            try {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(128, new SecureRandom(mKey.getBytes(Charsets.UTF8)));
                SecretKey secretKey = keyGenerator.generateKey();
                SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

                {
                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    byte[] args = new byte[cipher.getBlockSize()];
                    Arrays.fill(args, (byte) 0);
                    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(args));
                    mEncoder = cipher;
                }

                {
                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    byte[] args = new byte[cipher.getBlockSize()];
                    Arrays.fill(args, (byte) 0);
                    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(args));
                    mDecoder = cipher;
                }
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        public String encode(@Nullable String input, @Nullable String defaultValue) {
            if (input == null) {
                return defaultValue;
            }

            String noise = nextRandomNoise();
            input = wrap(input, noise);

            try {
                byte[] output = mEncoder.doFinal(input.getBytes(Charsets.UTF8));
                String encoded = Base64.encode(output);
                return addVersionFlag(encoded);
            } catch (Throwable e) {
                e.printStackTrace();
                Timber.e(e);
                return defaultValue;
            }
        }

        public String decode(@Nullable String input, @Nullable String defaultValue) {
            if (input == null) {
                return defaultValue;
            }

            if (!matchVersionFlag(input)) {
                Timber.v("version flag not match");
                return defaultValue;
            }

            input = removeVersionFlag(input);

            try {
                byte[] output = mDecoder.doFinal(Base64.decode(input));
                String decoded = new String(output, Charsets.UTF8);
                return unwrap(decoded);
            } catch (Throwable e) {
                e.printStackTrace();
                Timber.e(e);
                return defaultValue;
            }
        }

        @NonNull
        private String addVersionFlag(@NonNull String input) {
            return VERSION + ":" + input;
        }

        @NonNull
        private String removeVersionFlag(@NonNull String input) {
            return input.substring(VERSION.length());
        }

        private boolean matchVersionFlag(@NonNull String input) {
            return input.startsWith(VERSION + ":");
        }

        @NonNull
        private String wrap(@NonNull String input, @NonNull String noise) {
            return input + ":" + nextRandomNoise();
        }

        @NonNull
        private String unwrap(@NonNull String input) {
            int index = input.lastIndexOf(":");
            return input.substring(0, index);
        }

        @NonNull
        private String nextRandomNoise() {
            return String.valueOf(((int) (Math.random() * 1000)) % 10);
        }
    }

    public static AES getDefault() {
        return DEFAULT_INSTANCE.get();
    }

    private static final Singleton<AES> DEFAULT_INSTANCE = new Singleton<AES>() {

        private static final String DEFAULT_KEY = "67A8%24a1d5d11e9ab14dNC3bd8:73&d93";

        @Override
        protected AES create() {
            return new AES(DEFAULT_KEY);
        }
    };

}
