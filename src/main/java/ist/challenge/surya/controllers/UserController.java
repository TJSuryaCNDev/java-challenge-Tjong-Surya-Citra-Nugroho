package ist.challenge.surya.controllers;
import ist.challenge.surya.entities.User;
import ist.challenge.surya.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
  @Autowired
  private UserRepository userRepository;

  @GetMapping("/user")
  public ResponseEntity<List<User>> findAll() {
    try {
      List<User> user = userRepository.findAll();
      return new ResponseEntity<>(user, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody User user) {
    try {
      if(user.getUsername() == null || user.getPassword() == null) return new ResponseEntity<>("Username dan / atau password kosong", HttpStatus.BAD_REQUEST);
      if(user.getUsername().length() > 25) {
        user.setUsername(user.getUsername().substring(0, 25));
      }
      if(user.getPassword().length() > 5) {
        user.setPassword(user.getPassword().substring(0, 5));
      }
      
      Optional<User> getUser = userRepository.findByUsername(user.getUsername());
      if(!getUser.isPresent() || !getUser.get().getPassword().equals(user.getPassword())) {
        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
      }

      return new ResponseEntity<>("Sukses Login", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody User user) {
    try {
      if(user.getUsername() == null || user.getPassword() == null) return new ResponseEntity<>("Username dan / atau password kosong", HttpStatus.BAD_REQUEST);
      if(user.getUsername().length() > 25) {
        user.setUsername(user.getUsername().substring(0, 25));
      }
      if(user.getPassword().length() > 5) {
        user.setPassword(user.getPassword().substring(0, 5));
      }
      
      User newUser = new User();
      newUser.setUsername(user.getUsername());
      newUser.setPassword(user.getPassword());
      userRepository.save(newUser);
      return new ResponseEntity<>(null, HttpStatus.CREATED);
    } catch(DataIntegrityViolationException e) {
      return new ResponseEntity<>("Username sudah terpakai", HttpStatus.CONFLICT);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/user/{id}")
  public ResponseEntity<String> updateUser(@PathVariable(value = "id") long userId, @RequestBody User userDetails) {
    try {
      User user = userRepository.getReferenceById(userId);
      if(userDetails.getUsername() != null) {
        if(userDetails.getUsername().length() > 25) {
          userDetails.setUsername(userDetails.getUsername().substring(0, 25));
        }
        user.setUsername(userDetails.getUsername());
      }
      if(userDetails.getPassword() != null) {
        if(userDetails.getPassword().length() > 5) {
          userDetails.setPassword(userDetails.getPassword().substring(0, 5));
        }
        if(user.getPassword().equals(userDetails.getPassword())) {
          return new ResponseEntity<>("Password tidak boleh sama dengan password sebelumnya", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(userDetails.getPassword());
      }

      userRepository.save(user);
      return new ResponseEntity<>(null, HttpStatus.CREATED);
    } catch(DataIntegrityViolationException e) {
      return new ResponseEntity<>("Username sudah terpakai", HttpStatus.CONFLICT);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}