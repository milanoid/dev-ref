package io.keikai.devref.web;

import io.keikai.devref.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

import static io.keikai.devref.Configuration.pathCaseMap;

/**
 * /case/[CASE_NAME]
 * instantiate and start application class, {@link UseCase} according to case name.
 */
@WebServlet("/case/*")
public class CaseServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(request, resp);
        String caseName = request.getPathInfo().substring(1);
        Class appClass = pathCaseMap.get(caseName);
        if (appClass == null) {
            request.getRequestDispatcher("/notfound.jsp").forward(request, resp);
        } else {
            UseCase keikaiCase = newKeikaiCase(appClass);
            keikaiCase.init(keikaiServerAddress);
            // pass the anchor DOM element id for rendering keikai
            String keikaiJs = keikaiCase.getJavaScriptURI("spreadsheet");
            // store as an attribute to be accessed by EL on a JSP
            request.setAttribute(Configuration.KEIKAI_JS, keikaiJs);
            request.getRequestDispatcher("/mycase/case.jsp").forward(request, resp);
            keikaiCase.run();
        }
    }

    private UseCase newKeikaiCase(Class appClass) throws IOException {
        try {
            return (UseCase) appClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }
}
