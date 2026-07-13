package ao.co.atlantico.candidaturas.web;

import org.apache.jasper.JspC;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JspCompilationTest {

    @Test
    void todasAsPaginasJspSaoTraduzidasSemErros() throws Exception {
        Path output = Path.of("target", "jspc-test");
        Files.createDirectories(output);

        JspC compiler = new JspC();
        compiler.setUriroot(Path.of("src", "main", "webapp").toAbsolutePath().toString());
        compiler.setOutputDir(output.toAbsolutePath().toString());
        compiler.setJspFiles(String.join(",",
            "WEB-INF/views/candidatura-form.jsp",
            "WEB-INF/views/candidatura-success.jsp",
            "WEB-INF/views/candidatura-list.jsp",
            "WEB-INF/views/candidatura-detail.jsp",
            "WEB-INF/views/admin-login.jsp",
            "WEB-INF/views/error.jsp"
        ));
        compiler.setCompile(false);
        compiler.setFailOnError(true);
        compiler.execute();

        try (var generated = Files.walk(output)) {
            assertTrue(generated.anyMatch(path -> path.toString().endsWith(".java")));
        }
    }
}
