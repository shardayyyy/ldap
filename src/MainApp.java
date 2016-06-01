/**
 * Created by shardayyy on 5/25/16.
 */
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

public class MainApp
{
    static String LDAP_SERVER_URL = "ldap://ldap.forumsys.com:389";
    static String USER_CONTEXT = "dc=example,dc=com";

    public static void main(String[] args)
    {
        verifyUser("riemann", "password");
        verifyUser("gauss", "password");
    }

    public static void verifyUser(String userName, String password)
    {
        DirContext ctx = null;
        try
        {
            // creating environment for initial context
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, LDAP_SERVER_URL);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, "uid=" + userName + "," + USER_CONTEXT);
            env.put(Context.SECURITY_CREDENTIALS, password);

            // Create the initial context
            ctx = new InitialDirContext(env);
            System.out.println("\n\nAuthenticated: " + (ctx != null));

            // get more attributes about this user
            SearchControls scs = new SearchControls();
            scs.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String[] attrNames = { "mail", "cn", "objectClass" };
            scs.setReturningAttributes(attrNames);

            NamingEnumeration nes = ctx.search(USER_CONTEXT, "uid=" + userName, scs);
            if(nes.hasMore()) {
                Attributes attrs = ((SearchResult) nes.next()).getAttributes();
                System.out.println("mail: " + attrs.get("mail").get());
                System.out.println("cn: " + attrs.get("cn").get());
                //NamingEnumeration nes2 = attrs.get("objectClass").get().;
                System.out.println("objectClass: " + attrs.get("objectClass").get());
            }
        }
        catch (NamingException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (ctx != null)
                try {
                    ctx.close();
                } catch (NamingException ex) {}
        }
    }
}