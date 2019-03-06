package dwbh.api.services.internal;

import dwbh.api.domain.ErrorConstants;
import dwbh.api.domain.Login;
import dwbh.api.domain.LoginInput;
import dwbh.api.domain.Result;
import dwbh.api.domain.User;
import dwbh.api.repositories.UserRepository;
import dwbh.api.services.CryptoService;
import dwbh.api.services.SecurityService;
import java.util.Optional;
import javax.inject.Singleton;

/**
 * Service responsible to check the security constraints
 *
 * @since 0.1.0
 */
@Singleton
public class DefaultSecurityService implements SecurityService {

  private final transient CryptoService cryptoService;
  private final transient UserRepository userRepository;

  /**
   * Initializes security service with cryptographic service and user database access
   *
   * @param cryptoService service used to handle JWT tokens
   * @param userRepository service used to check user data constraints
   * @since 0.1.0
   */
  public DefaultSecurityService(CryptoService cryptoService, UserRepository userRepository) {
    this.cryptoService = cryptoService;
    this.userRepository = userRepository;
  }

  @Override
  public User findUserByToken(String token) {
    return cryptoService.verifyToken(token).map(userRepository::findByEmail).orElse(null);
  }

  @Override
  public Result<Login> login(LoginInput input) {
    User user = userRepository.findByEmail(input.getEmail());

    return Optional.ofNullable(user)
        .filter(user1 -> cryptoService.verifyWithHash(input.getPassword(), user1.getPassword()))
        .map(cryptoService::createToken)
        .map(token -> new Login(token, user))
        .map(Result::result)
        .orElse(Result.error(ErrorConstants.BAD_CREDENTIALS));
  }
}
