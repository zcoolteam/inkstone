package com.zcool.inkstone.security;

import com.zcool.inkstone.lang.Base64;
import com.zcool.inkstone.lang.Charsets;
import com.zcool.inkstone.lang.Singleton;
import com.zcool.inkstone.util.ContextUtil;
import com.zcool.inkstone.util.TextUtil;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AES {

    private final V1 mV1;

    public AES(@Nullable String key) {
        mV1 = new V1(key);
    }

    @NonNull
    public String encode(@Nullable String input) throws Exception {
        return mV1.encode(input);
    }

    @Nullable
    public String decode(@NonNull String input) throws Exception {
        return mV1.decode(input);
    }

    private static class V1 implements Encoder, Decoder {

        // version + split + noise + split + type + split + original string

        private static final String VERSION = "AES.V1";

        private static final String TYPE_NULL = "null";
        private static final String TYPE_EMPTY = "empty";
        private static final String TYPE_NORMAL = "normal";

        private static final String SPLIT = ":";

        private final String mKey;
        private final byte[] mKeyBytes;
        private final byte[] mIvBytes;

        private V1(@Nullable String key) {
            final String packageName = ContextUtil.getContext().getPackageName();
            TextUtil.checkStringNotEmpty(packageName, "package name not found");

            mKey = key + ";" + packageName;
            mKeyBytes = new byte[16];
            mIvBytes = new byte[16];
            fillBytes(mKey, mKeyBytes);
            fillBytes(packageName, mIvBytes);
        }

        private Cipher createEncoder() throws Exception {
            SecretKeySpec secretKeySpec = new SecretKeySpec(mKeyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(mIvBytes));
            return cipher;
        }

        private Cipher createDecoder() throws Exception {
            SecretKeySpec secretKeySpec = new SecretKeySpec(mKeyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(mIvBytes));
            return cipher;
        }

        @NonNull
        @Override
        public String encode(@Nullable String input) throws Exception {
            final String formatInput = wrapVersion(wrapNoise(wrapType(input)));
            byte[] inputBytes = formatInput.getBytes(Charsets.UTF8);
            byte[] outputBytes = createEncoder().doFinal(inputBytes);
            String output = Base64.encodeUrl(outputBytes);
            return wrapVersion(output);
        }

        @Nullable
        @Override
        public String decode(@NonNull String input) throws Exception {
            input = unwrapVersion(input);
            byte[] inputBytes = Base64.decode(input);
            byte[] outputBytes = createDecoder().doFinal(inputBytes);
            String output = new String(outputBytes, Charsets.UTF8);
            output = unwrapType(unwrapNoise(unwrapVersion(output)));
            return output;
        }

        @NonNull
        private String wrapType(@Nullable String str) {
            if (str == null) {
                return TYPE_NULL + SPLIT;
            }
            if (str.isEmpty()) {
                return TYPE_EMPTY + SPLIT;
            }
            return TYPE_NORMAL + SPLIT + str;
        }

        @Nullable
        private String unwrapType(@NonNull String str) {
            if (str.startsWith(TYPE_NULL + SPLIT)) {
                return null;
            }
            if (str.startsWith(TYPE_EMPTY + SPLIT)) {
                return "";
            }
            if (str.startsWith(TYPE_NORMAL + SPLIT)) {
                return str.substring((TYPE_NORMAL + SPLIT).length());
            }
            throw new RuntimeException("unknown type " + str.substring(0, Math.max(str.length(), 10)) + "[...]");
        }

        @NonNull
        private String wrapNoise(@NonNull String str) {
            return nextRandomNoise() + SPLIT + str;
        }

        @NonNull
        private String unwrapNoise(@NonNull String str) {
            return str.substring(str.indexOf(SPLIT) + 1);
        }

        @NonNull
        private String wrapVersion(@NonNull String str) {
            return VERSION + SPLIT + str;
        }

        private boolean matchVersion(@NonNull String str) {
            return str.startsWith(VERSION + SPLIT);
        }

        @NonNull
        private String unwrapVersion(@NonNull String str) {
            if (!matchVersion(str)) {
                throw new RuntimeException("unknown version " + str.substring(0, Math.max(str.length(), 10)) + "[...]");
            }
            return str.substring((VERSION + SPLIT).length());
        }

        @NonNull
        private String nextRandomNoise() {
            return String.valueOf((int) (Math.random() * 10000));
        }

        private void fillBytes(String str, final byte[] out) {
            try {
                final byte[] bytes = String.valueOf(str).getBytes(Charsets.UTF8);
                Arrays.fill(out, (byte) 0);

                for (int i = 0; i < out.length; i++) {
                    out[i] += bytes[i % bytes.length];
                }
                for (int i = 0; i < bytes.length; i++) {
                    out[i % out.length] += bytes[i];
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
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

    private interface Encoder {
        @NonNull
        String encode(@Nullable String input) throws Exception;
    }

    private interface Decoder {
        @Nullable
        String decode(@NonNull String input) throws Exception;
    }

}
