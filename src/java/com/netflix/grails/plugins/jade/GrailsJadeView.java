package com.netflix.grails.plugins.jade;


import de.neuland.jade4j.spring.view.JadeView;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest;
import org.codehaus.groovy.grails.web.util.WebUtils;
import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.exceptions.JadeException;
import de.neuland.jade4j.template.JadeTemplate;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.io.Writer;
import groovy.text.SimpleTemplateEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsApplication;

class GrailsJadeView extends JadeView {

    static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(GrailsJadeView.class);

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        model.put("request", WebObjectConverter.toMap(request));
        model.put("session", WebObjectConverter.toMap(request.getSession()));
        model.put("application", WebObjectConverter.toMap(request.getServletContext()));
        GrailsWebRequest webRequest = WebUtils.retrieveGrailsWebRequest();
        model.put("params", webRequest.getParams());
        model.put("flash", webRequest.getAttributes().getFlashScope(request));


        GrailsApplication grailsApplication = grails.util.Holders.getGrailsApplication();
        Object gTaglib = grailsApplication.getMainContext().getBean("org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib");
        Object assetTagLib = grailsApplication.getMainContext().getBean("asset.pipeline.AssetsTagLib");
        Map gspMap = new HashMap();     
        gspMap.put("g", gTaglib);
        gspMap.put("request", request);
        gspMap.put("asset", assetTagLib);       
        
        PrintWriter responseWriter = response.getWriter();
        Writer writer = new StringWriter();

        try {
            
            getConfiguration().renderTemplate(getTemplate(), model, writer);
            String htmlString = writer.toString();
            Object template = new SimpleTemplateEngine().createTemplate(htmlString).make(gspMap);
            responseWriter.write(template.toString());          

        } catch (JadeException e) {
            String htmlString = e.toHtmlString(writer.toString());
            responseWriter.write(htmlString);
            logger.error("failed to render template [" + getUrl() + "]", e);
        } catch (IOException e) {
            responseWriter.write("<pre>could not find template: " + getUrl() + "\n");
            e.printStackTrace(responseWriter);
            responseWriter.write("</pre>");
            logger.error("could not find template", e);
        }       

        //super.renderMergedOutputModel(model, request, response);
    }

}