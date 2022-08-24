package technify;

import org.junit.Test;
import technify.business.*;
import static org.junit.Assert.assertEquals;
import static technify.business.ReturnValue.*;


public class SimpleTest extends  AbstractTest {


    @Test
    public void testCreateTables()
    {
    createTables();
    }

    @Test
    public void testClearTables()
    {
        clearTables();
    }


    @Test
    public void simpleTestCreateUser()
    {
        User v = new User();
        v.setId(1);
        v.setName("Dani");
        v.setCountry("Israel");
        v.setPremium(true);
        ReturnValue ret= Solution.addUser(v);
        assertEquals(OK, ret);
    }

    @Test
    public void testDeleteUser(){
        User u = new User();
        u.setId(10);
        u.setName("Eli");
        u.setCountry("England");
        u.setPremium(true);

        ReturnValue ret = Solution.deleteUser(u);
        assertEquals(NOT_EXISTS , ret);

    }
}