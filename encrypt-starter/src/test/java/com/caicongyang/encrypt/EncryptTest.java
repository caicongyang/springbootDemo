package com.caicongyang.encrypt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptTest {

    private static final String SECRET_KEY = "test-key-16bytes";

    private static ObjectMapper mapper;

    @BeforeAll
    static void setUp() {
        AesUtils.setSecretKey(SECRET_KEY);
        mapper = new ObjectMapper();
    }

    // ---- DTOs ----

    public static class UserInfo {
        @EncryptField
        private String password;

        @Sensitive
        private String phone;

        @Sensitive(type = Sensitive.Type.EMAIL)
        private String email;

        @Sensitive(type = Sensitive.Type.ID_CARD)
        private String idCard;

        private String nickname;

        public UserInfo() {}

        public UserInfo(String password, String phone, String email, String idCard, String nickname) {
            this.password = password;
            this.phone = phone;
            this.email = email;
            this.idCard = idCard;
            this.nickname = nickname;
        }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getIdCard() { return idCard; }
        public void setIdCard(String idCard) { this.idCard = idCard; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
    }

    // ---- AES Utility Tests ----

    @Test
    void aesShouldRoundtripCorrectly() {
        String original = "mySecretPassword123";
        String encrypted = AesUtils.encrypt(original, SECRET_KEY);
        assertNotNull(encrypted);
        assertNotEquals(original, encrypted);

        String decrypted = AesUtils.decrypt(encrypted, SECRET_KEY);
        assertEquals(original, decrypted);
    }

    @Test
    void aesShouldHandleEmptyAndNull() {
        assertEquals("", AesUtils.encrypt("", SECRET_KEY));
        assertNull(AesUtils.encrypt(null, SECRET_KEY));
        assertEquals("", AesUtils.decrypt("", SECRET_KEY));
        assertNull(AesUtils.decrypt(null, SECRET_KEY));
    }

    @Test
    void aesShouldThrowOnDecryptWithWrongKey() {
        String encrypted = AesUtils.encrypt("hello", SECRET_KEY);
        assertThrows(RuntimeException.class, () ->
                AesUtils.decrypt(encrypted, "wrong-key-16bytes"));
    }

    @Test
    void aesShouldBeDeterministic() {
        String original = "deterministic-test";
        String enc1 = AesUtils.encrypt(original, SECRET_KEY);
        String enc2 = AesUtils.encrypt(original, SECRET_KEY);
        assertEquals(enc1, enc2);
    }

    // ---- Sensitive Masking Tests ----

    @Test
    void sensitiveShouldMaskPhone() throws JsonProcessingException {
        UserInfo user = new UserInfo("secret", "13812345678", "", "", "Jack");
        String json = mapper.writeValueAsString(user);
        assertTrue(json.contains("138****5678"), "phone should be masked: " + json);
        assertFalse(json.contains("13812345678"), "raw phone should not appear");
    }

    @Test
    void sensitiveShouldMaskEmail() throws JsonProcessingException {
        UserInfo user = new UserInfo("secret", "", "test@example.com", "", "Jack");
        String json = mapper.writeValueAsString(user);
        assertTrue(json.contains("t***@example.com"), "email should be masked: " + json);
        assertFalse(json.contains("test@example.com"), "raw email should not appear");
    }

    @Test
    void sensitiveShouldMaskIdCard() throws JsonProcessingException {
        UserInfo user = new UserInfo("secret", "", "", "310123198001011234", "Jack");
        String json = mapper.writeValueAsString(user);
        assertTrue(json.contains("310***********1234"), "idCard should be masked: " + json);
        assertFalse(json.contains("310123198001011234"), "raw idCard should not appear");
    }

    @Test
    void sensitiveShouldHandleNull() throws JsonProcessingException {
        UserInfo user = new UserInfo("secret", null, null, null, "Jack");
        String json = mapper.writeValueAsString(user);
        assertTrue(json.contains("\"phone\":null"), "phone should be null");
        assertTrue(json.contains("\"email\":null"), "email should be null");
        assertTrue(json.contains("\"idCard\":null"), "idCard should be null");
    }

    // ---- EncryptField Serializer Tests ----

    @Test
    void encryptFieldShouldEncryptValue() throws JsonProcessingException {
        UserInfo user = new UserInfo("myPassword", "13800001111", "a@b.com", "310123198001011234", "Jack");
        String json = mapper.writeValueAsString(user);
        assertFalse(json.contains("\"password\":\"myPassword\""), "password should be encrypted");
        assertTrue(json.contains("\"nickname\":\"Jack\""), "nickname should not be changed");
    }

    @Test
    void encryptFieldShouldRoundtrip() {
        String original = "sensitive-data";
        String encrypted = AesUtils.encrypt(original, SECRET_KEY);
        String decrypted = AesUtils.decrypt(encrypted, SECRET_KEY);
        assertEquals(original, decrypted);
    }

    @Test
    void encryptFieldShouldHandleNull() throws JsonProcessingException {
        UserInfo user = new UserInfo(null, null, null, null, "Jack");
        String json = mapper.writeValueAsString(user);
        assertTrue(json.contains("\"password\":null"));
    }

    // ---- Full Integration ----

    @Test
    void fullObjectShouldSerializeWithBothAnnotations() throws JsonProcessingException {
        UserInfo user = new UserInfo("P@ssw0rd!", "13987654321", "hello@world.org",
                "110101199001015678", "Codex");
        String json = mapper.writeValueAsString(user);

        assertFalse(json.contains("P@ssw0rd!"), "password must be encrypted");
        assertFalse(json.contains("13987654321"), "phone must be masked");
        assertFalse(json.contains("hello@world.org"), "email must be masked");
        assertFalse(json.contains("110101199001015678"), "idCard must be masked");
        assertTrue(json.contains("\"nickname\":\"Codex\""), "nickname should be plain");
    }

    // ---- Auto-Detection Tests ----

    public static class AutoDetectUser {
        @Sensitive
        private String field;

        public AutoDetectUser() {}
        public AutoDetectUser(String field) { this.field = field; }
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
    }

    @Test
    void sensitiveAutoDetectPhone() throws JsonProcessingException {
        AutoDetectUser user = new AutoDetectUser("15600002222");
        String json = mapper.writeValueAsString(user);
        assertTrue(json.contains("156****2222"), "auto-detect phone mask: " + json);
    }

    @Test
    void sensitiveAutoDetectEmail() throws JsonProcessingException {
        AutoDetectUser user = new AutoDetectUser("user@example.com");
        String json = mapper.writeValueAsString(user);
        assertTrue(json.contains("u***@example.com"), "auto-detect email mask: " + json);
    }

    @Test
    void sensitiveAutoDetectIdCard() throws JsonProcessingException {
        AutoDetectUser user = new AutoDetectUser("310123198001011234");
        String json = mapper.writeValueAsString(user);
        assertTrue(json.contains("310***********1234"), "auto-detect idCard mask: " + json);
    }
}
