package eu.anticom;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.coveo.nashorn_modules.FilesystemFolder;
import com.coveo.nashorn_modules.Require;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import lombok.Data;

import javax.script.*;
import java.io.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

public class Application {
    static ScheduledExecutorService globalScheduledThreadPool = Executors.newScheduledThreadPool(20);

    private Application.Args args = new Args();
    private JCommander commander;

    public Application(String[] argv) {
        commander = JCommander.newBuilder()
                .addObject(args)
                .programName("enginetest")
                .build();
        commander.parse(argv);
    }

    public void run() {
        if(args.isHelp()) {
            commander.usage();
            System.out.println("=============");
            System.out.println(args.getJsPath());
            return;
        }

        listEngines();
        installDeps();
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
        FilesystemFolder rootFolder = FilesystemFolder.create(new File(args.getJsFolder()), "UTF-8");

        ScriptContext ctx = new SimpleScriptContext();
        // Injection of __NASHORN_POLYFILL_TIMER__ in ScriptContext
        ctx.setAttribute("__NASHORN_POLYFILL_TIMER__", globalScheduledThreadPool, ScriptContext.ENGINE_SCOPE);

        try {
            // enable RequireJS module loading shim
            Require.enable(engine, rootFolder);
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        try {
            FileReader reader = new FileReader(args.getJsPath());
            engine.eval(reader/*, ctx*/);
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

    @Data
    public static class Args {
        @Parameter(description = "JS_ENTRYPOINT")
        private String jsPath = "src/main/javascript/index.js";

        @Parameter(names = {"--debug", "-d"}, description = "Debug mode")
        private boolean debug = false;

        @Parameter(names = {"--help", "-h"}, description = "Display this page", help = true)
        private boolean help;

        public String getJsFolder() {
            File file = new File(jsPath);
            if(file.isDirectory()) return file.toString();
            if(file.getParentFile() != null) return file.getParentFile().toString();
            return "UNKOWN";
        }
    }
}
