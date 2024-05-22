import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({ TrackTest.class, AlbumTest.class })
public class TestSuite {

}
