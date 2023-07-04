package mate.academy.service.impl;

import java.util.function.Predicate;
import mate.academy.exception.AuthenticationException;
import mate.academy.exception.RegistrationException;
import mate.academy.lib.Inject;
import mate.academy.lib.Service;
import mate.academy.model.User;
import mate.academy.service.AuthenticationService;
import mate.academy.service.UserService;
import mate.academy.util.HashUtil;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Inject
    private UserService userService;

    @Override
    public User login(String email, String password) throws AuthenticationException {
        return userService.findByEmail(email)
                .filter(getHashedPasswordCheckPredicate(password))
                .orElseThrow(() ->
                        new AuthenticationException("Can't login user with email: " + email));
    }

    @Override
    public User register(String email, String password) throws RegistrationException {
        if (password.isEmpty() || email.isEmpty()) {
            throw new RegistrationException(
                    "Can't registration new user because some field (password or email) is empty");
        }
        if (userService.findByEmail(email).isPresent()) {
            throw new RegistrationException("Can't register new user with existing email: "
                    + email);
        }
        User user = userService.add(new User(email, password));
        if (user == null) {
            throw new RegistrationException("Can't register new user with email: " + email);
        }
        return user;
    }

    private static Predicate<User> getHashedPasswordCheckPredicate(String password) {
        return u -> u.getPassword().equals(HashUtil.hashPassword(password, u.getSalt()));
    }
}
