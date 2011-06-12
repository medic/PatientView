package net.frontlinesms.plugins.patientview.data.repository;

import java.util.Collection;
import java.util.List;

import net.frontlinesms.plugins.patientview.data.domain.people.User;

public interface UserDao {

	// CRUD
	/**
	 * Saves a user, creating a new database record. This method does NOT check
	 * for duplicates.
	 * 
	 * @param s
	 */
	public void saveUser(User s);

	/**
	 * Soft deletes a user's record.
	 * 
	 * @param user
	 */
	public void deleteUser(User user, String reason);

	/**
	 * Updates an existing User's recprd in the database. This method does NOT
	 * check for duplicates.
	 * 
	 * @param s
	 */
	public void updateUser(User s);

	/**
	 * Returns all users
	 * 
	 * @return
	 */
	public Collection<User> getAllUsers();

	/**
	 * Finds all users with a username that has the parameter as a substring.
	 * This will return all users if passed an empty string
	 * 
	 * @param username
	 *            part or all of a username
	 * @return the list of users
	 */
	public List<User> findUsersByUsername(String username, boolean includeDeleted);

	/**
	 * Returns the user with the supplied username, or null if there is no user
	 * for the username. This method should not be confused with getUsersByName,
	 * which searches for users via their real names as opposed to this method,
	 * which searches with usernames.
	 * 
	 * @param username
	 * @return the user
	 */
	public User getUserByUsername(String username, boolean includeDeleted);

	/**
	 * Returns the user with the supplied ID, or null if there is none
	 * 
	 * @param id
	 * @return
	 */
	public User getUserById(long id);

	/**
	 * Returns a list of users that have 'nameFragment' in their name by running
	 * a 'like' query with the supplied string. The like query is constructed to
	 * be placement-agnostic (it doesn't care where the name fragment is in the
	 * name, it just cares whether or not its there).
	 * 
	 * @param nameFragment
	 * @param limit
	 *            The maximum number of results returned
	 * @return
	 */
	public List<User> findUsersByName(String nameFragment, int limit, boolean includeDeleted);

}
