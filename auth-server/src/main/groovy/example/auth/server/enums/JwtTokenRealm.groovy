package example.auth.server.enums

import com.fasterxml.jackson.annotation.JsonCreator

enum JwtTokenRealm {

    ACCESS_TOKEN('access_token', 'JWT access token'),
    REFRESH_TOKEN('refresh_token', 'JWT access token')

    private String value
    private String description

    JwtTokenRealm(String value, String description) {
        this.value = value
        this.description = description
    }

    @JsonCreator
    static JwtTokenRealm fromString(String stringValue) {
        JwtTokenRealm fromString = null
        for (JwtTokenRealm tokenRealm : JwtTokenRealm.values()) {
            if (tokenRealm.value.equalsIgnoreCase(stringValue)) {
                fromString = tokenRealm
                break
            }
        }
        if (!fromString) {
            throw new UnsupportedOperationException("Value $stringValue is not allowed for enum JwtTokenRealm")
        }
        fromString
    }

    @Override
    String toString() {
        return this.value
    }

    String getValue() {
        return value
    }

    String getDescription() {
        return description
    }
}