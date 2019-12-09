Link: https://github.com/hisahi/csbase-poster-creator
Installation instructions: This is a Spring Boot project and can be started via NetBeans.

FLAW 1: Broken Authentication

The authentication of users and identification of who they are in the application has serious flaws. In well designed applications, an user logs in with the username and password in order to authenticate themselves. Once the authentication is successful, a random and difficult-to-guess token is generated to the user that can be then used to authenticate the user; this way services do not need to make the user send the password with every request. The token is the valid until the user logs out.

However, in this application, the tokens are just the user ID. This makes it impossible to terminate sessions. In addition, the passwords are stored as plaintext in the server, without hashing and salting, which would be adequate. Password length is restricted to 12 characters and there is no protection against weak passwords.

There are several changes required to address these issues. For example, the minimum length of passwords should be set to around 10 characters (at the time of writing), the maximum length restriction should be removed, there should be further protection against weak passwords (such as those used in known breaches), passwords should be stored correctly by hashing and salting (such as with bcrypt), tokens should be per-session and random, and logging in should have a rate limit. Many online services lack at least one of these features.

FLAW 2: Sensitive Data Exposure 

The service does not have any support for HTTPS, meaning that all sensitive information is carried over a plaintext channel.

In addition, authentication tokens are simply the user IDs. This is made into a critical problem by the fact that user IDs are simply sequential, allowing anyone to log in as another user simply by incrementing or decrementing the number in the cookie. This allows full access to the user’s details, including payment information, which too is stored completely unencrypted in the database and could therefore also be exposed by a breach. However, not even a breach is necessary with the current configuration.

Authentication tokens need to be randomized, generated using a cryptographically secure random number generator and be of sufficient length. This prevents forging cookies in order to access another user’s details.

In addition, sensitive information, such as payment information, should not be stored unencrypted in the database, its encryption should be secure, and it should only be decrypted once it is actually required. The encryption key should be stored separately in order to avoid it from being exposed.

In addition, the application must support HTTPS in order to encrypt sensitive data in transit.

FLAW 3: Security Misconfiguration

The configuration is nearly default in the application, save for a file size limitation (which has been increased from the default). However, further changes have been taken that worsen the security of the application. An example of this is that CSRF (cross-site request forgery) protection is completely disabled, which allows form requests to be easily forged by scripts running in other websites, as they can simply simulate POST requests through AJAX.

In addition, the token cookie lacks Secure or HttpOnly flags, allowing to be accessed and modified by JavaScript and accessed even when the connection is not secure (over HTTPS).

The above issues can be fixed by further configuring the application to be more secure. HTTPS should be enabled, and only strong ciphers should be permitted. CSRF protection should be enabled, and the token provided by such needs to be included in every form so that all valid POST requests to the application include it. The version of Spring Boot used is also old, and it should be updated.

FLAW 4: Broken Access Control

Access control, in any shape or form, is practically absent from the application. There is little in the way of preventing any user from doing anything they want through the backend. For example, private posters have the implication that only the user who added them can actually display them. However, this is not the case; any user can access and view these posters, if they know the URL, and since the URL only contains the poster ID, too sequential, it is easy to access any poster, regardless of whether they have been set as private or not.

In addition, the only protection against accessing other users’ details is the access token, which, as stated before, is easy to guess for any other user. In practice, this means that the user only needs to change the number in a cookie to access a list of that user’s posters.

Adding true access control should considerably improve the security of the application. Private posters should only be accessible to the user that created them, with other users getting an error page instead. Tokens should be more secure. In case of a separate admin interface ever being added, such as for removing inappropriate posters, the admin interface should have proper access control.

FLAW 5: Cross-site scripting (XSS)

Poster titles and descriptions have absolutely no protection against arbitrary HTML tags. This allows a malicious user to enter any HTML code under poster titles and descriptions, which are then interpreted by the browser whenever viewing any page that shows the title of any poster, such as poster lists and poster info pages. While this feature can be used to format both of these fields, they also have no protection whatsoever against using script tags, which allows any user to write possibly malicious JavaScript code that is then executed by the browser of any unsuspecting visitor.

To prevent cross-site scripting, all HTML tags should be escaped whenever displaying any value that the user can specify. In this case, poster titles and descriptions should be escaped either at the moment of input or when displayed on a page. For formatting, a set of separate formatting options can be added. Preventing only the <script> tag is an insufficient and incomplete solution that fails to solve the core problem.


