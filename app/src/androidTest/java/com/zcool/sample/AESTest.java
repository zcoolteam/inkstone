package com.zcool.sample;

import android.content.Context;

import com.zcool.inkstone.Inkstone;
import com.zcool.inkstone.security.AES;
import com.zcool.inkstone.util.AssetUtil;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class AESTest {

    @Test
    public void testDefaultAES() throws Throwable {
        Context context = InstrumentationRegistry.getTargetContext();
        Inkstone.init(context);

        List<String> inputs = AssetUtil.readAllLines("AESTestData", null, null);

        inputs.forEach(input -> {
            try {
                String encode = AES.getDefault().encode(input);
                String decode = AES.getDefault().decode(encode);
                System.out.println(input + " >>>>> " + encode + " >>>>> " + decode);
                Assert.assertEquals(input, decode);
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

}
