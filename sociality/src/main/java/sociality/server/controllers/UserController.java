package sociality.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import sociality.server.model.User;
import sociality.server.resolvers.JsonArg;
import sociality.server.services.UserService;

@Controller
@RequestMapping(value = "/user")
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/current", method = RequestMethod.GET)
	@ResponseBody
	public User get(User currentUser) {
		return userService.getRefreshedCurrentUser(currentUser.getId());
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public User update(@JsonArg(required = false) String username, @JsonArg(required = false) String firstName,
			@JsonArg(required = false) String lastName, @JsonArg(required = false) String password, User currentUser) {
		return userService.update(currentUser.getId(),username,firstName,lastName,password);
	}

}
