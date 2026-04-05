package ru.netology.test;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static ru.netology.data.SQLHelper.cleanDataBase;

public class LoginTest {
    LoginPage loginPage;
    DataHelper.AuthInfo authInfo = DataHelper.getAuthInfoWithTestData();

    @AfterAll
    static void tearDownAll() {
        cleanDataBase();
    }

    @BeforeEach
    void setup() {
        loginPage = open("http://localhost:9999", LoginPage.class);
    }

    @SneakyThrows
    @Test
    void shouldSuccesfulLogin(){
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode.getCode());
    }
    @Test
    void shouldGetErrorNotificationIfLoginRandom(){
        var authInfo = DataHelper.generateRandomUsers();
        loginPage.login(authInfo);
        loginPage.verifyErrorNotification("Ошибка!\nНеверно указан логин или пароль");
    }
    @Test
    void shouldGetErrorNotificationIfRandomVerificationCode(){
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotification("Ошибка! Неверно указан код! Попробуйте ещё раз.");
    }
    @Test
    void shouldBlockAfterThreeInvalidAttemps(){
        var authInfo = DataHelper.getAuthInfoWithTestData();
        for (int i = 0; i <3; i++){
            loginPage.login(new DataHelper.AuthInfo(authInfo.getLogin(), "123"));
            loginPage.verifyErrorNotification("Ошибка! Неверно указан логин или пароль");
        }
        loginPage.login(authInfo);
        loginPage.verifyErrorNotification("Ошибка! Пользователь заблокирован.");
    }

}

