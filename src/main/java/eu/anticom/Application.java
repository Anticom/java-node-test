package eu.anticom;

import com.coveo.nashorn_modules.FilesystemFolder;
import com.coveo.nashorn_modules.Require;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.util.List;
import java.util.function.Supplier;

public class Application {
    private String[] args;

    public Application(String[] args) {
        this.args = args;
    }

    public void run() {
        listEngines();
        //installDeps();
        runJsTest();
    }

    public void listEngines() {
        ScriptEngineManager manager = new ScriptEngineManager();
        List<ScriptEngineFactory> factories = manager.getEngineFactories();
        for (ScriptEngineFactory factory : factories) {
            System.out.println(factory.getEngineName());
            System.out.println(factory.getEngineVersion());
            System.out.println(factory.getLanguageName());
            System.out.println(factory.getLanguageVersion());
            System.out.println(factory.getExtensions());
            System.out.println(factory.getMimeTypes());
            System.out.println(factory.getNames());
            System.out.println("------------------");
        }
    }

    public void installDeps() {
        Supplier<Boolean> isWindows = () -> System.getProperty("os.name").toLowerCase().contains("win");
        String npm = isWindows.get() ? "npm.cmd" : "npm";

        System.out.println("Installing npm dependencies...");

        try {
            Process process = new ProcessBuilder(npm, "install")
                    .directory(new File("src/main/javascript/"))
                    .start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runJsTest() {
        //ScriptEngineManager manager = new ScriptEngineManager();
        //ScriptEngine engine = manager.getEngineByExtension("js");

        //NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        //ScriptEngine engine = factory.getScriptEngine("--language=es6");

        NashornScriptEngine engine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");
        FilesystemFolder rootFolder = FilesystemFolder.create(new File("src/main/javascript/"), "UTF-8");
        try {
            Require.enable(engine, rootFolder);
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        try {
            FileReader reader = new FileReader("src/main/javascript/index.js");
            engine.eval(reader);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Application app = new Application(args);

        try {
            app.run();
        } catch (Exception e) {
            System.err.println("### Unexpected exception ###");
            e.printStackTrace(System.err);
        }
    }
}
