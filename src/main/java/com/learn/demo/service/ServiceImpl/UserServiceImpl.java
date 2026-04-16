package com.learn.demo.service.ServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.learn.demo.model.User;
import com.learn.demo.repository.UserRepository;
import com.learn.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

        @Override
    public List<User> searchUsers(String username, String role) {
        return repository.searchUsers(username, role);
    }

    @Override
    public User saveUser(User user) {
        return repository.save(user);
    }
    @Override   
    public List<User> getAllUsers() {
        return repository.findAll();
    }
    @Override
    public User getUserById(Long userId) {
        return repository.findById(userId).orElse(null);
    }

    @Override
    public User updateUser (Long userId,User newUser){
        User user=repository.findById(userId).orElse(null);

        if(user != null) {
            user.setUserName(newUser.getUserName());
            user.setUserEmail(newUser.getUserEmail());
            user.setUserPassword(newUser.getUserPassword());
            // user.setUserId(newUser.getUserId());
            return repository.save(user);
        }
        return null;
    }


//     @Override
// public User updateUser(Long id, User newUser) {

//     User existingUser = repository.findById(id).orElse(null);

//     if (existingUser != null) {

//         // Copy only non-null values
//         if (newUser.getUserName() != null)
//             existingUser.setUserName(newUser.getUserName());

//         if (newUser.getUserEmail() != null)
//             existingUser.setUserEmail(newUser.getUserEmail());

//         if (newUser.getUserPassword() != null)
//             existingUser.setUserPassword(newUser.getUserPassword());

//         if (newUser.getUserRole() != null)
//             existingUser.setUserRole(newUser.getUserRole());

//         return repository.save(existingUser);
//     }

//     return null;
// }

    public void deleteUser(Long userId) {
        repository.deleteById(userId);
    }

}
