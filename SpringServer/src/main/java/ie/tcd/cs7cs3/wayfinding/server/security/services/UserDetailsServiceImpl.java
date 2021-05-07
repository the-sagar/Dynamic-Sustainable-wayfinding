package ie.tcd.cs7cs3.wayfinding.server.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ie.tcd.cs7cs3.wayfinding.server.model.User;
import ie.tcd.cs7cs3.wayfinding.server.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(emailId)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with emailId: " + emailId));
		return UserDetailsImpl.build(user);
	}
}
