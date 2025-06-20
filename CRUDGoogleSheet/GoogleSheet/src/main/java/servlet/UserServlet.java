package servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import service.SheetService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = "/user/*")
public class UserServlet extends HttpServlet {
    private SheetService sheetsService;
    private Gson gson = new Gson();

    // Put your actual Google Sheet ID here
    private final String SPREADSHEET_ID = "12KU3yRYF28gpXYMm22DoPaCnrFm0_T4YynM3n8HNzdk";

    @Override
    public void init() throws ServletException {  //here sheet service ka obj k liye override kiya nhi toh default ho jata
        try {
            sheetsService = new SheetService(SPREADSHEET_ID);
            System.out.println("init check time");
        } catch (Exception e) {
            throw new ServletException("Google Sheets service init failed", e);
        }
    }

    private String getIdFromPath(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            return null;
        }
        return pathInfo.substring(1); // Remove leading '/'
    }

//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        resp.setContentType("application/json");
//        String id = getIdFromPath(req);
//
//        System.out.println("Request = " +req);  //request ka hashcode print
//        printRequestDetails(req);
//        if (id == null) {
//            // Return all users as JSON
//            List<User> users = sheetsService.getUsers();
//            resp.getWriter().write(gson.toJson(users));
//        } else {
//            // You can implement lookup by ID here if you want
//            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            resp.getWriter().write("{\"error\":\"User not found\"}");
//        }
//    }


    // lamba code + html + complex code +  readabletry + code maintain complex == so jsp (java sarver Page came)
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        resp.setContentType("text/html");
//        String id = getIdFromPath(req);
//        PrintWriter out = resp.getWriter();
//
//        out.println("<html><body>");
//
//        if (id == null) {
//            List<User> users = sheetsService.getUsers();
//            out.println("<h1>User List</h1><ul>");
//            for (User user : users) {
//                out.printf("<li>ID: %s, Name: %s, Email: %s</li>", user.getId(), user.getName(), user.getEmail());
//            }
//            out.println("</ul>");
//        }
//        else {
//            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            out.println("<h1>User not found</h1>");
//        }
//
//        out.println("</body></html>");
//        System.out.println("do get hit time");
//    }

    @Override //path parameter param
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String id = getIdFromPath(req); // Extract ID from /user/{id}

        // Debug logs
        System.out.println("👉 doGet() called");
        System.out.println("Method = " + req.getMethod());
        System.out.println("Request URI = " + req.getRequestURI());
        System.out.println("Extracted ID = " + id);

        if (id == null) {
            // === GET /user ===
            List<User> users = sheetsService.getUsers();
            resp.getWriter().write(gson.toJson(users));
        } else {
            // === GET /user/{id} ===
            List<User> users = sheetsService.getUsers();
            for (User user : users) {
                if (user.getId().equals(id)) {
                    resp.getWriter().write(gson.toJson(user));
                    return;
                }
            }
            // If user not found
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"User with ID " + id + " not found\"}");
        }
    }



    @Override // body k throught
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader= req.getReader();
        User user = gson.fromJson(reader,User.class);
        System.out.println("📥 Received POST data: " + gson.toJson(user));
        if (user == null || user.getId() == null || user.getName() == null || user.getEmail() == null || user.getNumber() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid user data\"}");
            return;
        }
        User createdUser = sheetsService.addUser(user);
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(createdUser));
    }


    private void printRequestDetails(HttpServletRequest req) {
        System.out.println("------ HTTP REQUEST DETAILS ------");
        System.out.println("Method: " + req.getMethod());
        System.out.println("Request URI: " + req.getRequestURI());
        System.out.println("Request URL: " + req.getRequestURL());
        System.out.println("Protocol: " + req.getProtocol());
        System.out.println("Remote Addr: " + req.getRemoteAddr());
        System.out.println("Remote Host: " + req.getRemoteHost());
        System.out.println("Remote Port: " + req.getRemotePort());
        System.out.println("Query String: " + req.getQueryString());
        System.out.println("Path Info: " + req.getPathInfo());
        System.out.println("Context Path: " + req.getContextPath());

        // Print headers
        System.out.println("----- Headers -----");
        var headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            System.out.println(header + ": " + req.getHeader(header));
        }

        // Print parameters
        System.out.println("----- Parameters -----");
        req.getParameterMap().forEach((key, value) ->
                System.out.println(key + ": " + String.join(", ", value))
        );

        // Print attributes
        System.out.println("----- Attributes -----");
        var attrNames = req.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String attr = attrNames.nextElement();
            Object val = req.getAttribute(attr);
            System.out.println(attr + ": " + val);
        }

        System.out.println("------ END OF REQUEST ------");
    }

    @Override //using query ? : or Query Parameter
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("i");
        System.out.println("Id for delete"+id);
        if(id == null){
            resp.getWriter().write("id missing not able to delete");
            return;
        }
        try {
            boolean del = sheetsService.delete(id);
            if(del){
                resp.getWriter().println("User successfully deleted birdu");
            }
            else{
                resp.getWriter().write("Error in deletions");
            }

        }catch (IOException io){
            resp.getWriter().write("Error" +io.getMessage());
        }
    }
}
