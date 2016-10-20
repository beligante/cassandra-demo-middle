package almeida.rochapaulo.demo.filters;

import java.util.Objects;
import java.util.function.Predicate;

import almeida.rochapaulo.demo.entities.UserProfile;

public class ProfileFilters {

	private ProfileFilters() {
		super();
	}
	
	public static Predicate<? super UserProfile> byFirstName(String firstName) {
		return profile -> (firstName == null) ? true : contains(profile.getFirstName(), firstName);
	}
	
	public static Predicate<? super UserProfile> byLastname(String lastName) {
		return profile -> (lastName == null) ? true : contains(profile.getLastName(), lastName);
	}
	
	public static Predicate<? super UserProfile> byEmail(String email) {
		return profile -> (email == null) ? true : Objects.equals(email, profile.getEmail());
	}
	
	private static boolean contains(String source, String filter) {
		return source.contains(filter);
	}
	
}
