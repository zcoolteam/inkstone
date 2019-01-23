package com.zcool.sample;

import android.content.Context;

import com.zcool.inkstone.Inkstone;
import com.zcool.inkstone.security.AES;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class AESTest {

    @Test
    public void useAppContext() {
        Context context = InstrumentationRegistry.getTargetContext();
        Inkstone.init(context);

        String[] inputs = {
                "null",
                "",
                "0",
                "true",
                "{\"name\":\"peny\", \"age\":2}",
                "鹅鹅鹅，曲项向天歌",
                "==;&%^*<>《》｀、|"
        };
        String[] outputs = new String[inputs.length];

        for (int i = 0; i < inputs.length; i++) {
            outputs[i] = AES.getDefault().encode(inputs[i], "default value");

            System.out.print(inputs[i] + " -> ");
            System.out.print(outputs[i]);

            String result = AES.getDefault().decode(outputs[i], "default result value");
            System.out.println(" -> " + result);

            Assert.assertEquals(inputs[i], result);
        }
    }

}
