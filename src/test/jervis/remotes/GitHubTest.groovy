package jervis.remotes
//the GitHubTest() class automatically sees the GitHub() class because they're in the same package
import org.junit.*

class GitHubTest extends GroovyTestCase {
    def mygh
    def url
    //Mock the HTTP calls to the GitHub API and use resource files instead.
    //http://flyingtomoon.com/tag/mocking/
    //http://groovy.329449.n5.nabble.com/Groovy-metaclass-invokeConstructor-td5716360.html
    def mock(Class<URL> clazz) {
        def mc = clazz.metaClass
        mc.invokeMethod = { String name, args ->
            mc.getMetaMethod(name, args).invoke(delegate, args)
        }
        mc.getProperty = { String name  ->
            mc.getMetaProperty(name).getProperty(delegate)
        }
        mc.constructor = { String url ->
            this.url = url
            def constructor = delegate.getConstructor([String] as Class[])
            constructor.newInstance(url)
        }
        mc.newReader = {
            //create a file from the URL including the domain and path with all special characters and path separators replaced with an underscore
            String file = this.url.toString().replaceAll(/[:?=]/,"_").split('/')[2..-1].join('_')
            URL resource_url = this.getClass().getResource("/mocks/${file}");
            def resource = new File(resource_url.getFile())
            if(resource.isFile()) {
                return resource.newReader()
            }
            else {
                throw new RuntimeException("[404] Not Found - ${resource}")
            }
        }
    }
    //set up before every test
    @Before protected void setUp() {
        mock(URL)
        super.setUp()
        mygh = new GitHub()
    }
    //tear down after every test
    @After protected void tearDown() {
        mygh = null
        super.tearDown()
    }
    //test GitHub().gh_web
    @Test public void test_GitHub_set1Gh_web() {
        mygh.gh_web = "http://server"
        assert mygh.gh_web == "http://server/"
        assert mygh.gh_api == "http://server/api/v3/"
    }
    @Test public void test_GitHub_set2Gh_web() {
        mygh.gh_web = "http://server/"
        assert mygh.gh_web == "http://server/"
        assert mygh.gh_api == "http://server/api/v3/"
    }
    @Test public void test_GitHub_getGh_web() {
        assert mygh.gh_web == "https://github.com/"
    }
    //test GitHub().gh_api
    @Test public void test_GitHub_set1Gh_api() {
        mygh.gh_api = "http://server"
        assert mygh.gh_api == "http://server/"
    }
    @Test public void test_GitHub_set2Gh_api() {
        mygh.gh_api = "http://server/"
        assert mygh.gh_api == "http://server/"
    }
    @Test public void test_GitHub_getGh_api() {
        assert mygh.gh_api == "https://api.github.com/"
    }
    //test GitHub().gh_clone
    @Test public void test_GitHub_set1Gh_clone() {
        mygh.gh_clone = "http://server"
        assert mygh.gh_clone == "http://server/"
    }
    @Test public void test_GitHub_set2Gh_clone() {
        mygh.gh_clone = "http://server/"
        assert mygh.gh_clone == "http://server/"
    }
    @Test public void test_GitHub_getGh_clone() {
        assert mygh.gh_clone == "git://github.com/"
    }
    //test GitHub().gh_token
    @Test public void test_GitHub_set1Gh_token() {
        mygh.gh_token = "a"
        assert mygh.gh_token == "a"
    }
    @Test public void test_GitHub_set2Gh_token() {
        mygh.gh_token = ""
        assert mygh.gh_token == null
    }
    //test GitHub().getWebUrl()
    @Test public void test_GitHub_getWebUrl1() {
        assert mygh.getWebUrl() == mygh.gh_web
        assert mygh.getWebUrl() == "https://github.com/"
    }
    @Test public void test_GitHub_getWebUrl2() {
        mygh.gh_web = "http://server/"
        assert mygh.getWebUrl() == "http://server/"
    }
    //test GitHub().getCloneUrl()
    @Test public void test_GitHub_getCloneUrl1() {
        assert mygh.getCloneUrl() == mygh.gh_clone
        assert mygh.getCloneUrl() == "git://github.com/"
    }
    @Test public void test_GitHub_getCloneUrl2() {
        mygh.gh_clone = "http://server/"
        assert mygh.getCloneUrl() == "http://server/"
    }
    //test GitHub().toString()
    @Test public void test_GitHub_toString1() {
        assert mygh.toString() == "GitHub"
    }
    @Test public void test_GitHub_toString2() {
        mygh.gh_web = "http://server/"
        assert mygh.toString() == "GitHub Enterprise"
    }
    @Test public void test_GitHub_branches() {
        assert ['gh-pages', 'master'] == mygh.branches('samrocketman/jervis')
    }
    @Test public void test_GitHub_getFile() {
        assert 'language: groovy\n' == mygh.getFile('samrocketman/jervis','.travis.yml','master')
    }
    @Test public void test_GitHub_getFolderListing() {
        assert ['.gitignore', '.travis.yml', 'LICENSE', 'README.md', 'build.gradle', 'src'] == mygh.getFolderListing('samrocketman/jervis','/','master')
        assert ['main', 'resources', 'test'] == mygh.getFolderListing('samrocketman/jervis','src','master')
    }
}
