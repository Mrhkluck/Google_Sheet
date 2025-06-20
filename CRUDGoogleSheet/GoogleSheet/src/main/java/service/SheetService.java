package service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import model.User;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.*;

public class SheetService {
    private static final String APPLICATION_NAME = "CRUD SHEET";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private Sheets sheetsService;
    private final String SheetID;
    private final String SheetName = "User";

    public SheetService(String sheetID) throws GeneralSecurityException, IOException {
        this.SheetID = sheetID;
        init();
    }

    private void init() throws GeneralSecurityException, IOException {
        // Load credentials.json from classpath (src/main/resources)
        try (InputStream serviceAccountStream = getClass().getClassLoader().getResourceAsStream("credentials.json")) {
            if (serviceAccountStream == null) {
                throw new IOException("credentials.json not found in classpath");
            }

            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream)
                    .createScoped(List.of("https://www.googleapis.com/auth/spreadsheets"));

            sheetsService = new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    new HttpCredentialsAdapter(credentials)
            )
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
    }

    public List<User> getUsers() throws IOException {
        String range = SheetName + "!A2:D";
        ValueRange response = sheetsService.spreadsheets().values().get(SheetID, range).execute();
        List<List<Object>> rows = response.getValues();
        List<User> users = new ArrayList<>();

        if (rows == null || rows.isEmpty()) return users;

        for (List<Object> row : rows) {
            String id = row.size() > 0 ? row.get(0).toString() : "";
            String name = row.size() > 1 ? row.get(1).toString() : "";
            String email = row.size() > 2 ? row.get(2).toString() : "";
            String number = row.size() > 3 ? row.get(3).toString() : "";

            users.add(new User(id, name, email, number));
        }
        return users;
    }

    //add user
//    public User addUser(User user) throws IOException {
//        List<Object> data = Arrays.asList(user.getName(),user.getEmail(),user.getId(),user.getNumber());//list whiose value can't be change
//        ValueRange body  = new ValueRange().setValues(Collections.singletonList(data));
//        sheetsService.spreadsheets().values()
//                .append(SheetID,SheetName + "!A:D",body)
//                .setValueInputOption("USER_ENTERED").execute();
//        return user;
//    }

    public User addUser(User user) throws IOException {
        List<Object> data = Arrays.asList(
                user.getId(), user.getName(), user.getEmail(), user.getNumber()
        );

        ValueRange body = new ValueRange().setValues(Collections.singletonList(data));
        var result = sheetsService.spreadsheets().values()
                .append(SheetID, SheetName + "!A:D", body)
                .setValueInputOption("USER_ENTERED").execute();

        System.out.println("📤 Google Sheets Append Response: " + result);  // ✅ Log response for debug
        return user;
    }

    public User getId(String id) throws IOException {
        if (id != null) {
            List<User> users = getUsers();
            for (User u : users) {
                if (u.getId().equals(id)) return u;
            }
        }
        return null;
    }

    public int getRowId(String id) throws IOException {
        if (id != null) {
            ValueRange vr = sheetsService.spreadsheets().values().get(SheetID, SheetName + "!A2:D").execute();
            List<List<Object>> rows = vr.getValues();
            if (rows != null) {
                int rowIndex = 2;
                for (List<Object> row : rows) {
                    if (row.size() > 0 && row.get(0).toString().equals(id)) {
                        return rowIndex;
                    }
                    rowIndex++;
                }
            }
        }
        return -1;
    }

    public boolean delete(String id) throws IOException {
        int row = getRowId(id);
        if (row == -1) {
            return false;
        }
        BatchUpdateSpreadsheetRequest br = new BatchUpdateSpreadsheetRequest()
                .setRequests(List.of(new Request().setDeleteDimension(new DeleteDimensionRequest()
                        .setRange(new DimensionRange()
                                .setSheetId(getSheetId())
                                .setDimension("ROWS")
                                .setStartIndex(row - 1)
                                .setEndIndex(row)
                        ))));
        sheetsService.spreadsheets().batchUpdate(SheetID, br).execute();
        return true;
    }

    private Integer getSheetId() throws IOException {
        var spreadsheet = sheetsService.spreadsheets().get(SheetID).execute();
        var sheets = spreadsheet.getSheets();
        for (var sheet : sheets) {
            if (sheet.getProperties().getTitle().equals(SheetName)) {
                return sheet.getProperties().getSheetId();
            }
        }
        throw new IOException("Sheet not found: " + SheetName);
    }

}
