package com.example.api.tests;

import com.example.config.ApiConfig;
import io.qameta.allure.*;
import io.qameta.allure.model.Status;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Epic("File Operations API Tests")
@Feature("Upload and Download Operations")
@Story("Тестирование загрузки и скачивания файлов")
@Tag("file-operations")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileOperationsTests {

    private Long testPetId;
    private File testImageFile;
    private Path tempDir;

    @BeforeAll
    @Step("Подготовка тестовых ресурсов")
    public void setupAll() throws IOException {
        Allure.label("layer", "api");
        Allure.label("component", "file-operations");
        tempDir = Paths.get("target/test-files/" + UUID.randomUUID());
        Files.createDirectories(tempDir);
        testImageFile = createTestImageFile();

        Allure.addAttachment("Настройки теста", "text/plain",
                "Временная директория: " + tempDir.toAbsolutePath() + "\n" +
                        "Тестовый файл: " + testImageFile.getAbsolutePath() + "\n" +
                        "Размер файла: " + testImageFile.length() + " bytes");
    }

    @BeforeEach
    @Step("Создание тестового питомца")
    public void setupTestPet() {
        String petJson = """
            {
                "id": 999999,
                "name": "TestPetForUpload",
                "status": "available"
            }
            """;

        Response response = given(ApiConfig.getRequestSpec())
                .body(petJson)
                .when()
                .post("/pet")
                .then()
                .log().all()
                .extract()
                .response();
        JsonPath jsonPath = response.jsonPath();
        testPetId = jsonPath.getLong("id");

        Allure.parameter("Pet ID", String.valueOf(testPetId));
        Allure.addAttachment("Созданный питомец", "application/json", response.getBody().asString());
    }

    @AfterEach
    @Step("Очистка тестового питомца")
    public void cleanupTestPet() {
        if (testPetId != null) {
            try {
                given(ApiConfig.getRequestSpec())
                        .pathParam("petId", testPetId)
                        .when()
                        .delete("/pet/{petId}")
                        .then()
                        .log().all();

                Allure.step("Питомец удален: ID " + testPetId);
            } catch (Exception e) {
                Allure.step("Ошибка при удалении питомца", Status.FAILED);
            }
        }
    }

    @AfterAll
    @Step("Очистка тестовых ресурсов")
    public void cleanupAll() throws IOException {
        if (testImageFile != null && testImageFile.exists()) {
            testImageFile.delete();
        }
        if (Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted((a, b) -> -a.compareTo(b))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                        }
                    });
        }

        Allure.step("Ресурсы очищены");
    }

//    @Test
//    @Story("Загрузка файла на сервер")
//    @Description("Тест проверяет загрузку изображения для питомца через multipart/form-data")
//    @Severity(SeverityLevel.CRITICAL)
//    @DisplayName("API: Загрузка изображения питомца")
//    @Tag("upload")
//    public void testUploadImageToPet() {
//        String additionalMetadata = "Тестовое изображение для загрузки";
//        Allure.parameter("Additional Metadata", additionalMetadata);
//        Allure.addAttachment("Тестовый файл", "image/png",
//                Files.readAllBytes(testImageFile.toPath()));
//        Response response = given(ApiConfig.getMultipartRequestSpec())
//                .pathParam("petId", testPetId)
//                .multiPart("file", testImageFile, "image/png")
//                .multiPart("additionalMetadata", additionalMetadata)
//                .when()
//                .post("/pet/{petId}/uploadImage")
//                .then()
//                .log().all()
//                .extract()
//                .response();
//        JsonPath jsonPath = response.jsonPath();
//        response.then()
//                .statusCode(200);
//        assertEquals(200, jsonPath.getInt("code"),
//                "Код в теле ответа должен быть 200");
//        assertEquals("unknown", jsonPath.getString("type"),
//                "Тип должен быть 'unknown'");
//        String message = jsonPath.getString("message");
//        assertNotNull(message, "Сообщение не должно быть null");
//        assertTrue(message.length() > 0, "Сообщение не должно быть пустым");
//        assertTrue(message.contains("additionalMetadata") || message.contains(testImageFile.getName()),
//                "Сообщение должно содержать информацию о файле или метаданных");
//        response.then()
//                .body("code", equalTo(200))
//                .body("type", equalTo("unknown"))
//                .body("message", not(emptyOrNullString()));
//
//        Allure.addAttachment("Ответ загрузки", "application/json", response.getBody().asString());
//        Allure.addAttachment("JSONPath анализ", "text/plain",
//                "Code: " + jsonPath.getInt("code") + "\n" +
//                        "Type: " + jsonPath.getString("type") + "\n" +
//                        "Message length: " + message.length() + " chars\n" +
//                        "Contains metadata: " + message.contains(additionalMetadata) + "\n" +
//                        "Contains filename: " + message.contains(testImageFile.getName()));
//
//        Allure.step("✅ Изображение успешно загружено");
//    }

//    @Test
//    @Story("Скачивание файла с сервера")
//    @Description("Тест проверяет скачивание файла и сохранение на диск")
//    @Severity(SeverityLevel.CRITICAL)
//    @DisplayName("API: Скачивание файла")
//    @Tag("download")
//    public void testDownloadFile() throws IOException {
//        given(ApiConfig.getMultipartRequestSpec())
//                .pathParam("petId", testPetId)
//                .multiPart("file", testImageFile, "image/png")
//                .multiPart("additionalMetadata", "Файл для скачивания")
//                .when()
//                .post("/pet/{petId}/uploadImage")
//                .then()
//                .statusCode(200);
//
//        Path downloadedFilePath = tempDir.resolve("downloaded-file.bin");
//
//        Allure.parameter("Файл для сохранения", downloadedFilePath.toString());
//
//        Response downloadResponse = given(ApiConfig.getRequestSpec())
//                .pathParam("petId", testPetId)
//                .when()
//                .get("/pet/{petId}")
//                .then()
//                .log().all()
//                .extract()
//                .response();
//
//        byte[] responseBytes = downloadResponse.getBody().asByteArray();
//        Files.write(downloadedFilePath, responseBytes);
//
//        assertTrue(Files.exists(downloadedFilePath),
//                "Файл должен быть создан после скачивания");
//        assertTrue(Files.size(downloadedFilePath) > 0,
//                "Скачанный файл не должен быть пустым");
//
//        String downloadedContent = new String(responseBytes);
//        assertTrue(downloadedContent.contains("\"id\":" + testPetId) ||
//                        downloadedContent.contains("TestPetForUpload"),
//                "Скачанные данные должны содержать информацию о питомце");
//
//        downloadResponse.then()
//                .statusCode(200);
//
//        JsonPath jsonPath = downloadResponse.jsonPath();
//        assertNotNull(jsonPath.get("id"), "Ответ должен содержать поле 'id'");
//        assertNotNull(jsonPath.get("name"), "Ответ должен содержать поле 'name'");
//        assertNotNull(jsonPath.get("status"), "Ответ должен содержать поле 'status'");
//
//        Allure.addAttachment("Скачанный файл", "application/octet-stream",
//                Files.readAllBytes(downloadedFilePath));
//
//        Allure.addAttachment("Информация о скачивании", "text/plain",
//                "Путь к файлу: " + downloadedFilePath.toAbsolutePath() + "\n" +
//                        "Размер файла: " + Files.size(downloadedFilePath) + " bytes\n" +
//                        "Содержит ID питомца: " + downloadedContent.contains("\"id\":" + testPetId) + "\n" +
//                        "Содержит имя питомца: " + downloadedContent.contains("TestPetForUpload"));
//
//        Allure.step("✅ Файл успешно скачан и сохранен");
//    }

    @Step("Создание тестового PNG файла")
    private File createTestImageFile() throws IOException {
        Path filePath = tempDir.resolve("test-upload-image.png");
        byte[] pngData = new byte[] {
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                0x00, 0x00, 0x00, 0x0D,
                0x49, 0x48, 0x44, 0x52,
                0x00, 0x00, 0x00, 0x01,
                0x00, 0x00, 0x00, 0x01,
                0x08, 0x02, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44,
                (byte) 0xAE, 0x42, 0x60, (byte) 0x82
        };

        Files.write(filePath, pngData);
        return filePath.toFile();
    }
}