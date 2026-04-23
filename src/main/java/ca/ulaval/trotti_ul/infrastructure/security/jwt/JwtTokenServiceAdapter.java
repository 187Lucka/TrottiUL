package ca.ulaval.trotti_ul.infrastructure.security.jwt;

import ca.ulaval.trotti_ul.application.auth.DecodedToken;
import ca.ulaval.trotti_ul.application.auth.JwtClaims;
import ca.ulaval.trotti_ul.application.auth.TokenService;

public class JwtTokenServiceAdapter implements TokenService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    public JwtTokenServiceAdapter(JwtEncoder encoder, JwtDecoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    @Override
    public String encode(JwtClaims claims) {
        return encoder.encode(claims);
    }

    @Override
    public DecodedToken decode(String token) {
        DecodedJwt decoded = decoder.decode(token);
        return new DecodedToken(decoded.subject(), decoded.roles(), decoded.technicianId());
    }
}
