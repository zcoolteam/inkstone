package com.zcool.inkstone.processor;

import com.google.auto.service.AutoService;
import com.zcool.inkstone.annotation.ApplicationDelegate;
import com.zcool.inkstone.annotation.ServicesProvider;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

@AutoService(Processor.class)
public class InkstoneGenProcessor extends AbstractProcessor {

    private Map<String, TypeElement> mApplicationDelegateElements = new HashMap<>();
    private Map<String, TypeElement> mServicesProviderElements = new HashMap<>();

    private File mManifestFile;
    private String mVariantDirName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        try {
            FileObject fileObject = processingEnv.getFiler().getResource(StandardLocation.SOURCE_OUTPUT, "", "_inkstone_tmp_" + System.currentTimeMillis());
            String filePath = fileObject.toUri().getPath();

            // debug
            // debug/myflavor
            // release/Flavor1Flavor2
            String variantDirName = null;

            String tmpFilename;
            File tmpFile = new File(filePath);
            tmpFile = tmpFile.getParentFile();
            tmpFilename = tmpFile.getName();

            while (!"apt".equals(tmpFilename)) {
                if (variantDirName == null) {
                    variantDirName = tmpFilename;
                } else {
                    variantDirName = tmpFilename + File.separator + variantDirName;
                }
                tmpFile = tmpFile.getParentFile();
                tmpFilename = tmpFile.getName();
            }

            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "apt dir is: " + tmpFile.getAbsolutePath() + " variantDirName is: " + variantDirName);
            mVariantDirName = variantDirName;

            // now tmpFile is apt dir
            mManifestFile = new File(tmpFile.getParentFile(), "buildInkstone" + File.separator + variantDirName + File.separator + "AndroidManifest.xml");
            // mManifestFile = new File(new File(filePath).getParentFile(), "AndroidManifest.xml");
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Manifest file path: " + mManifestFile.getAbsolutePath());
            mManifestFile.getParentFile().mkdirs();
        } catch (Throwable e) {
            throw new RuntimeException("fail to create AndroidManifest.xml", e);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        {
            Set<? extends Element> appDelegateElements = roundEnv.getElementsAnnotatedWith(ApplicationDelegate.class);
            if (appDelegateElements != null) {
                for (Element item : appDelegateElements) {
                    if (!ElementKind.CLASS.equals(item.getKind())) {
                        throw new IllegalArgumentException(ApplicationDelegate.class.getName() + " must set on class");
                    }

                    TypeElement typeItem = (TypeElement) item;
                    String targetClass = typeItem.getQualifiedName().toString();
                    Object old = mApplicationDelegateElements.put(targetClass, typeItem);
                    if (old != null) {
                        throw new RuntimeException("InkstoneGenProcessor process found duplicate ApplicationDelegate class " + targetClass);
                    }

                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "InkstoneGenProcessor process found ApplicationDelegate class " + targetClass);
                }
            }
        }

        {
            Set<? extends Element> servicesProviderElements = roundEnv.getElementsAnnotatedWith(ServicesProvider.class);
            if (servicesProviderElements != null) {
                for (Element item : servicesProviderElements) {
                    if (!ElementKind.CLASS.equals(item.getKind())) {
                        throw new IllegalArgumentException(ServicesProvider.class.getName() + " must set on class");
                    }

                    TypeElement typeItem = (TypeElement) item;
                    String targetClass = typeItem.getQualifiedName().toString();
                    Object old = mServicesProviderElements.put(targetClass, typeItem);
                    if (old != null) {
                        throw new RuntimeException("InkstoneGenProcessor process found duplicate ServicesProvider class " + targetClass);
                    }

                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "InkstoneGenProcessor process found ServicesProvider class " + targetClass);
                }
            }
        }

        if (roundEnv.processingOver()) {
            genManifest();
        }

        return false;
    }

    private void genManifest() {
        if (mManifestFile.exists()) {
            if (!mManifestFile.delete()) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "InkstoneGenProcessor fail to delete Manifest file " + mManifestFile);
            }
        }

        try {
            if (!mManifestFile.createNewFile()) {
                throw new IllegalAccessException("Manifest createNewFile return false " + mManifestFile);
            }
        } catch (Throwable e) {
            throw new RuntimeException("fail to create Manifest file", e);
        }

        try (OutputStream os = new FileOutputStream(mManifestFile)) {
            writeManifestContent(os);
        } catch (Throwable e) {
            throw new RuntimeException("fail to write Manifest content", e);
        }
    }

    private void writeManifestContent(OutputStream os) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        document.setXmlStandalone(true);

        org.w3c.dom.Element manifestNode = document.createElement("manifest");
        manifestNode.setAttribute("xmlns:android", "http://schemas.android.com/apk/res/android");

        org.w3c.dom.Element applicationNode = document.createElement("application");
        org.w3c.dom.Element innerActivityNode = document.createElement("activity");

        innerActivityNode.setAttribute("android:name", "com.zcool.inkstone.app.InkstoneInnerActivity");
        innerActivityNode.setAttribute("android:exported", "false");

        org.w3c.dom.Element innerIntentFilterNode = document.createElement("intent-filter");
        for (Map.Entry<String, TypeElement> item : mApplicationDelegateElements.entrySet()) {
            org.w3c.dom.Element applicationDelegateActionNode = document.createElement("action");
            applicationDelegateActionNode.setAttribute("android:name", "inkstone#ApplicationDelegate#" + item.getValue().getAnnotation(ApplicationDelegate.class).priority() + "#" + item.getKey());
            innerIntentFilterNode.appendChild(applicationDelegateActionNode);
        }

        manifestNode.appendChild(applicationNode);
        applicationNode.appendChild(innerActivityNode);
        innerActivityNode.appendChild(innerIntentFilterNode);

        document.appendChild(manifestNode);

        TransformerFactory tff = TransformerFactory.newInstance();
        Transformer tf = tff.newTransformer();
        tf.setOutputProperty(OutputKeys.INDENT, "yes");

        tf.transform(new DOMSource(document), new StreamResult(os));
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();

        types.add(ApplicationDelegate.class.getName());
        types.add(ServicesProvider.class.getName());

        return Collections.unmodifiableSet(types);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
