/*
   Copyright 2014-2018 Sam Gleske - https://github.com/samrocketman/jervis

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
   */
package net.gleske.jervis.lang
//the lifecycleGeneratorTest() class automatically sees the lifecycleGenerator() class because they're in the same package
import net.gleske.jervis.exceptions.JervisException
import net.gleske.jervis.exceptions.PlatformValidationException
import net.gleske.jervis.exceptions.SecurityException
import net.gleske.jervis.exceptions.UnsupportedLanguageException
import net.gleske.jervis.exceptions.UnsupportedToolException
import net.gleske.jervis.lang.lifecycleValidator
import net.gleske.jervis.lang.platformValidator
import net.gleske.jervis.lang.toolchainValidator
import net.gleske.jervis.tools.securityIO
import org.junit.After
import org.junit.Before
import org.junit.Test

class lifecycleGeneratorTest extends GroovyTestCase {
    def generator
    //set up before every test
    @Before protected void setUp() {
        super.setUp()
        generator = new lifecycleGenerator()
        URL url = this.getClass().getResource('/good_lifecycles_simple.json');
        generator.loadLifecycles(url.getFile())
        url = this.getClass().getResource('/good_toolchains_simple.json');
        generator.loadToolchains(url.getFile())
    }
    //tear down after every test
    @After protected void tearDown() {
        generator = null
        super.tearDown()
    }
    //loadLifecycles() tests
    @Test public void test_lifecycleGenerator_loadLifecycles_isNull_instantiate() {
        generator = new lifecycleGenerator()
        assert generator.lifecycle_obj == null
    }
    @Test public void test_lifecycleGenerator_loadLifecycles_type_checking() {
        assert generator.lifecycle_obj != null
        assert generator.lifecycle_obj.class == lifecycleValidator
    }
    @Test public void test_lifecycleGenerator_loadLifecycles_simple_query() {
        assert generator.lifecycle_obj.lifecycles['groovy']['friendlyName'] == 'Groovy'
    }
    @Test public void test_lifecycleGenerator_loadLifecycles_throws_exception() {
        generator = new lifecycleGenerator()
        URL url = this.getClass().getResource('/bad_lifecycles_missing_defaultKey.json');
        shouldFail(JervisException) {
            generator.loadLifecycles(url.getFile())
        }
    }
    //loadLifecyclesString() tests
    @Test public void test_lifecycleGenerator_loadLifecyclesString_type_checking() {
        generator = new lifecycleGenerator()
        URL url = this.getClass().getResource('/good_lifecycles_simple.json');
        String contents = new File(url.getFile()).getText()
        generator.loadLifecyclesString(contents)
        assert generator.lifecycle_obj != null
        assert generator.lifecycle_obj.class == lifecycleValidator
    }
    @Test public void test_lifecycleGenerator_loadLifecyclesString_simple_query() {
        generator = new lifecycleGenerator()
        URL url = this.getClass().getResource('/good_lifecycles_simple.json');
        String contents = new File(url.getFile()).getText()
        generator.loadLifecyclesString(contents)
        assert generator.lifecycle_obj.lifecycles['groovy']['friendlyName'] == 'Groovy'
    }
    @Test public void test_lifecycleGenerator_loadLifecyclesString_throws_exception() {
        generator = new lifecycleGenerator()
        URL url = this.getClass().getResource('/bad_lifecycles_missing_defaultKey.json');
        String contents = new File(url.getFile()).getText()
        shouldFail(JervisException) {
            generator.loadLifecyclesString(contents)
        }
    }
    //loadToolchains() tests
    @Test public void test_lifecycleGenerator_loadToolchains_isNull_instantiate() {
        generator = new lifecycleGenerator()
        assert generator.toolchain_obj == null
    }
    @Test public void test_lifecycleGenerator_loadToolchains_type_checking() {
        assert generator.toolchain_obj != null
        assert generator.toolchain_obj.class == toolchainValidator
    }
    @Test public void test_lifecycleGenerator_loadToolchains_simple_query() {
        assert generator.toolchain_obj.toolchains['jdk']['default_ivalue'] == 'openjdk7'
    }
    @Test public void test_lifecycleGenerator_loadToolchains_throws_exception() {
        generator = new lifecycleGenerator()
        URL url = this.getClass().getResource('/bad_toolchains_missing_default_ivalue.json');
        shouldFail(JervisException) {
            generator.loadToolchains(url.getFile())
        }
    }
    //loadToolchainsString() tests
    @Test public void test_lifecycleGenerator_loadToolchainsString_type_checking() {
        generator = new lifecycleGenerator()
        URL url = this.getClass().getResource('/good_toolchains_simple.json');
        String contents = new File(url.getFile()).getText()
        generator.loadToolchainsString(contents)
        assert generator.toolchain_obj != null
        assert generator.toolchain_obj.class == toolchainValidator
    }
    @Test public void test_lifecycleGenerator_loadToolchainsString_simple_query() {
        generator = new lifecycleGenerator()
        URL url = this.getClass().getResource('/good_toolchains_simple.json');
        String contents = new File(url.getFile()).getText()
        generator.loadToolchainsString(contents)
        assert generator.toolchain_obj.toolchains['jdk']['default_ivalue'] == 'openjdk7'
    }
    @Test public void test_lifecycleGenerator_loadToolchainsString_throws_exception() {
        generator = new lifecycleGenerator()
        URL url = this.getClass().getResource('/bad_toolchains_missing_default_ivalue.json');
        String contents = new File(url.getFile()).getText()
        shouldFail(JervisException) {
            generator.loadToolchainsString(contents)
        }
    }
    //loadYaml() tests
    @Test public void test_lifecycleGenerator_loadYaml_exception_no_lifecycles__loaded() {
        generator = new lifecycleGenerator()
        shouldFail(JervisException) {
            generator.loadYamlString('language: ruby')
        }
    }
    @Test public void test_lifecycleGenerator_loadYaml_exception_no_toolchains_loaded() {
        generator = new lifecycleGenerator()
        URL url = this.getClass().getResource('/good_lifecycles_simple.json');
        generator.loadLifecycles(url.getFile())
        shouldFail(JervisException) {
            generator.loadYamlString('language: ruby')
        }
    }
    @Test public void test_lifecycleGenerator_loadYaml_supportedLanguage_yes() {
        generator.loadYamlString('language: ruby')
        assert 'ruby' == generator.yaml_language
    }
    @Test public void test_lifecycleGenerator_loadYaml_unsupportedLanguage_none() {
        //not in lifecycles and not in toolchains
        shouldFail(UnsupportedLanguageException) {
            generator.loadYamlString('language: derp')
        }
    }
    @Test public void test_lifecycleGenerator_loadYaml_unsupportedLanguage_toolchains() {
        //in lifecycles but not in toolchains
        shouldFail(UnsupportedLanguageException) {
            generator.loadYamlString('language: groovy')
        }
    }
    @Test public void test_lifecycleGenerator_setfolder_listing_throws_exception() {
        //did not call loadYamlString() first
        shouldFail(JervisException) {
            generator.folder_listing = ['Gemfile.lock', 'Gemfile']
        }
    }
    @Test public void test_lifecycleGenerator_setfolder_listing_ruby_gemfile_lock() {
        generator.loadYamlString('language: ruby')
        generator.folder_listing = ['Gemfile.lock', 'Gemfile']
        assert 'rake1' == generator.lifecycle_key
    }
    @Test public void test_lifecycleGenerator_setfolder_listing_ruby_gemfile() {
        generator.loadYamlString('language: ruby')
        generator.folder_listing = ['Gemfile']
        assert 'rake2' == generator.lifecycle_key
    }
    @Test public void test_lifecycleGenerator_setfolder_listing_java_ant_fallback() {
        generator.loadYamlString('language: java')
        generator.folder_listing = []
        assert 'ant' == generator.lifecycle_key
    }
    @Test public void test_lifecycleGenerator_isMatrixBuild_false() {
        generator.loadYamlString('language: ruby\nenv: foo=bar')
        assert false == generator.isMatrixBuild()
        generator.loadYamlString('language: ruby\nenv:\n  - foo=bar')
        assert false == generator.isMatrixBuild()
        generator.loadYamlString('language: ruby\nenv:\n  matrix:\n    - foo=bar')
        assert false == generator.isMatrixBuild()
    }
    @Test public void test_lifecycleGenerator_isMatrixBuild_true() {
        generator.loadYamlString('language: ruby\nenv:\n  - foobar=foo\n  - foobar=bar')
        assert true == generator.isMatrixBuild()
        generator.loadYamlString('language: ruby\nenv:\n  matrix:\n    - foobar=foo\n    - foobar=bar')
        assert true == generator.isMatrixBuild()
    }
    @Test public void test_lifecycleGenerator_matrixExcludeFilter() {
        generator.loadYamlString('language: ruby\nenv: [world=hello, world=goodbye]\nrvm: ["1.9.3", "2.0.0", "2.1"]\nmatrix:\n  exclude:\n    - env: world=goodbye\n      rvm: "2.1"')
        assert '!(env == \'env1\' && rvm == \'rvm2\')' == generator.matrixExcludeFilter()
        generator.loadYamlString('language: ruby\nenv: [world=hello, world=goodbye]\nrvm: ["1.9.3", "2.0.0", "2.1"]\nmatrix:\n  include:\n    - env: world=goodbye\n      rvm: "2.1"')
        assert '(env == \'env1\' && rvm == \'rvm2\')' == generator.matrixExcludeFilter()
        generator.loadYamlString('language: ruby\njdk: openjdk6\nenv: [world=hello, world=goodbye]\nrvm: ["1.9.3", "2.0.0", "2.1"]\nmatrix:\n  include:\n    - env: world=goodbye\n      rvm: "2.1"\n    - jdk: openjdk6')
        assert '(env == \'env1\' && rvm == \'rvm2\')' == generator.matrixExcludeFilter()
        generator.loadYamlString('language: ruby\nenv: [world=hello, world=goodbye]\nrvm: ["1.9.3", "2.0.0", "2.1"]\nmatrix:\n  exclude:\n    - env: world=goodbye\n      rvm: "2.1"\n    - env: world=hello\n      rvm: 1.9.3')
        assert '!(env == \'env1\' && rvm == \'rvm2\') && !(env == \'env0\' && rvm == \'rvm0\')' == generator.matrixExcludeFilter()
        generator.loadYamlString('language: ruby\nenv: [world=hello, world=goodbye]\nrvm: ["1.9.3", "2.0.0", "2.1"]\nmatrix:\n  include:\n    - env: world=goodbye\n      rvm: "2.1"\n    - env: world=hello\n      rvm: 1.9.3')
        assert '((env == \'env1\' && rvm == \'rvm2\') || (env == \'env0\' && rvm == \'rvm0\'))' == generator.matrixExcludeFilter()
        generator.loadYamlString('language: ruby\nenv: [world=hello, world=goodbye]\nrvm: ["1.9.3", "2.0.0", "2.1"]\nmatrix:\n  exclude:\n    - env: world=hello\n      rvm: 1.9.3\n  include:\n    - rvm: "1.9.3"\n    - rvm: "2.1"')
        assert '!(env == \'env0\' && rvm == \'rvm0\') && ((rvm == \'rvm0\') || (rvm == \'rvm2\'))' == generator.matrixExcludeFilter()
        generator.loadYamlString('language: ruby\nenv: [world=hello, world=goodbye]\nrvm: ["1.9.3", "2.0.0", "2.1"]\nmatrix:\n  include:\n    - env: world=hello\n      rvm: 1.9.3\n  exclude:\n    - rvm: "1.9.3"\n    - rvm: "2.1"')
        assert '!(rvm == \'rvm0\') && !(rvm == \'rvm2\') && (env == \'env0\' && rvm == \'rvm0\')' == generator.matrixExcludeFilter()
        generator.loadYamlString('language: ruby\nenv:\n  matrix: [world=hello, world=goodbye]\nrvm: ["1.9.3", "2.0.0", "2.1"]\nmatrix:\n  exclude:\n    - env: world=hello\n      rvm: 1.9.3\n  include:\n    - rvm: "1.9.3"\n    - rvm: "2.1"')
        assert '!(env == \'env0\' && rvm == \'rvm0\') && ((rvm == \'rvm0\') || (rvm == \'rvm2\'))' == generator.matrixExcludeFilter()
        generator.loadYamlString('language: ruby\nenv:\n  matrix: [world=hello, world=goodbye]\nrvm: ["1.9.3", "2.0.0", "2.1"]\nmatrix:\n  include:\n    - env: world=hello\n      rvm: 1.9.3\n  exclude:\n    - rvm: "1.9.3"\n    - rvm: "2.1"')
        assert '!(rvm == \'rvm0\') && !(rvm == \'rvm2\') && (env == \'env0\' && rvm == \'rvm0\')' == generator.matrixExcludeFilter()
        generator.loadYamlString('language: ruby\nenv:\n  matrix: [world=hello, world=goodbye]\nrvm: ["1.9.3", "2.0.0", "2.1"]\nmatrix:\n  include:\n    - rvm: 1.9.3\n      env: world=hello\n  exclude:\n    - rvm: "1.9.3"\n    - rvm: "2.1"')
        assert '!(rvm == \'rvm0\') && !(rvm == \'rvm2\') && (rvm == \'rvm0\' && env == \'env0\')' == generator.matrixExcludeFilter()
        //test friendly labels
        URL url = this.getClass().getResource('/good_toolchains_friendly.json');
        generator.loadToolchains(url.getFile())
        generator.loadYamlString('language: ruby\nenv:\n  matrix: [world=hello, world=goodbye]\nrvm: ["1.9.3", "2.0.0", "2.1"]\nmatrix:\n  include:\n    - rvm: 1.9.3\n      env: world=hello\n  exclude:\n    - rvm: "1.9.3"\n    - rvm: "2.1"')
        assert '!(rvm == \'rvm:1.9.3\') && !(rvm == \'rvm:2.1\') && (rvm == \'rvm:1.9.3\' && env == \'env0\')' == generator.matrixExcludeFilter()
    }
    @Test public void test_lifecycleGenerator_matrixGetAxisValue1() {
        generator.loadYamlString('language: ruby\nenv:\n  - foobar=foo\n  - foobar=bar')
        assert 'env0 env1' == generator.matrixGetAxisValue('env')
    }
    @Test public void test_lifecycleGenerator_matrixGetAxisValue2() {
        generator.loadYamlString('language: ruby\nenv:\n  - foobar=foo\n  - foobar=bar')
        assert '' == generator.matrixGetAxisValue('rvm')
    }
    @Test public void test_lifecycleGenerator_matrixGetAxisValue3() {
        generator.loadYamlString('language: ruby\nenv:\n  matrix:\n    - foobar=foo\n    - foobar=bar')
        assert 'env0 env1' == generator.matrixGetAxisValue('env')
    }
    @Test public void test_lifecycleGenerator_matrixGetAxisValue_friendly() {
        URL url = this.getClass().getResource('/good_toolchains_friendly.json');
        generator.loadToolchains(url.getFile())
        generator.loadYamlString('language: ruby\nenv:\n  matrix: [world=hello, world=goodbye]\nrvm: ["1.9.3", "2.0.0", "2.1"]')
        assert 'env0 env1' == generator.matrixGetAxisValue('env')
        assert 'rvm:1.9.3 rvm:2.0.0 rvm:2.1' == generator.matrixGetAxisValue('rvm')
    }
    @Test public void test_lifecycleGenerator_generateToolchainSection_matrix() {
        //basic env matrix
        generator.loadYamlString('language: ruby\nenv: [world=hello, world=goodbye]')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\ncase ${env} in\n  env0)\n    export world=hello\n    ;;\n  env1)\n    export world=goodbye\n    ;;\nesac\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n' == generator.generateToolchainSection()
        //using jdk as matrix
        generator.loadYamlString('language: ruby\njdk: [openjdk6, openjdk7]')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\ncase ${jdk} in\n  jdk0)\n    more commands\n    ;;\n  jdk1)\n    some commands\n    ;;\nesac\n' == generator.generateToolchainSection()
        //advanced env section which has a matrix section
        generator.loadYamlString('language: ruby\nenv:\n  matrix: [world=hello, world=goodbye]')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\ncase ${env} in\n  env0)\n    export world=hello\n    ;;\n  env1)\n    export world=goodbye\n    ;;\nesac\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n' == generator.generateToolchainSection()
        generator.loadYamlString('language: ruby\nenv:\n  matrix: {hello: three}')
        shouldFail(UnsupportedToolException) {
            generator.generateToolchainSection()
        }
        ///advanced env section which has a matrix and global section
        generator.loadYamlString('language: ruby\nenv:\n  global: foobar=foo\n  matrix: [world=hello, world=goodbye]')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\nexport foobar=foo\ncase ${env} in\n  env0)\n    export world=hello\n    ;;\n  env1)\n    export world=goodbye\n    ;;\nesac\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n' == generator.generateToolchainSection()
        generator.loadYamlString('language: ruby\nenv:\n  global: [foobar=foo, foobar=bar]\n  matrix: [world=hello, world=goodbye]')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\nexport foobar=foo\nexport foobar=bar\ncase ${env} in\n  env0)\n    export world=hello\n    ;;\n  env1)\n    export world=goodbye\n    ;;\nesac\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n' == generator.generateToolchainSection()
        generator.loadYamlString('language: ruby\nenv:\n  global: {hello: three}')
        shouldFail(UnsupportedToolException) {
            generator.generateToolchainSection()
        }
        //check for bad value in jdk matrix
        generator.loadYamlString('language: ruby\njdk: [openjdk6, openjdk7, derp]')
        shouldFail(UnsupportedToolException) {
            generator.generateToolchainSection()
        }
        //test for friendly labels
        URL url = this.getClass().getResource('/good_toolchains_friendly.json');
        generator.loadToolchains(url.getFile())
        generator.loadYamlString('language: ruby\njdk: [openjdk6, openjdk7]')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\ncase ${jdk} in\n  jdk:openjdk6)\n    more commands\n    ;;\n  jdk:openjdk7)\n    some commands\n    ;;\nesac\n' == generator.generateToolchainSection()
    }
    @Test public void test_lifecycleGenerator_generateToolchainSection_nonmatrix() {
        //basic language test
        generator.loadYamlString('language: ruby')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n' == generator.generateToolchainSection()
        //configuring basic env toolchain
        generator.loadYamlString('language: ruby\nenv: foo=bar')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\nexport foo=bar\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n' == generator.generateToolchainSection()
        //test for env toolchain as an ArrayList
        generator.loadYamlString('language: ruby\nenv:\n  - foo=bar')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\nexport foo=bar\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n' == generator.generateToolchainSection()
        //configuring basic jdk toolchain
        generator.loadYamlString('language: ruby\njdk: openjdk7')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n' == generator.generateToolchainSection()
        //testing env global value for non-matrix configuration
        generator.loadYamlString('language: ruby\nenv:\n  global: foo=bar')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\nexport foo=bar\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n' == generator.generateToolchainSection()
        generator.loadYamlString('language: ruby\nenv:\n  global:\n    - foo=bar\n    - hello=world')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\nexport foo=bar\nexport hello=world\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n' == generator.generateToolchainSection()
        generator.loadYamlString('language: ruby\nenv:\n  global: [foo=bar]')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\nexport foo=bar\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n' == generator.generateToolchainSection()
        //testing for env global throwing an exception for non-matrix configuration
        generator.loadYamlString('language: ruby\nenv:\n  global: {hello: three}')
        shouldFail(UnsupportedToolException) {
            generator.generateToolchainSection()
        }
        //testing env matrix value for non-matrix configuration
        generator.loadYamlString('language: ruby\nenv:\n  matrix: foo=bar')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\nexport foo=bar\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n' == generator.generateToolchainSection()
        generator.loadYamlString('language: ruby\nenv:\n  matrix: [foo=bar]')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\nexport foo=bar\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n' == generator.generateToolchainSection()
        //testing for exceptions
        generator.loadYamlString('language: ruby\njdk: derp')
        shouldFail(UnsupportedToolException) {
            generator.generateToolchainSection()
        }
        generator.loadYamlString('language: ruby\njdk: 2.5')
        shouldFail(UnsupportedToolException) {
            generator.generateToolchainSection()
        }
    }
    @Test public void test_lifecycleGenerator_generateSection_beforeInstall() {
        generator.loadYamlString('language: ruby')
        assert '' == generator.generateSection('before_install')
        generator.loadYamlString('language: ruby\nbefore_install: some code')
        assert '#\n# BEFORE_INSTALL SECTION\n#\nset +x\necho \'# BEFORE_INSTALL SECTION\'\nset -x\nsome code\n' == generator.generateSection('before_install')
        generator.loadYamlString('language: ruby\nbefore_install:\n - some code\n - more code')
        assert '#\n# BEFORE_INSTALL SECTION\n#\nset +x\necho \'# BEFORE_INSTALL SECTION\'\nset -x\nsome code\nmore code\n' == generator.generateSection('before_install')
    }
    @Test public void test_lifecycleGenerator_generateSection_install() {
        generator.loadYamlString('language: ruby')
        assert '#\n# INSTALL SECTION\n#\nset +x\necho \'# INSTALL SECTION\'\nset -x\nbundle install --jobs=3 --retry=3\n' == generator.generateSection('install')
        generator.loadYamlString('language: ruby\ninstall: some code')
        assert '#\n# INSTALL SECTION\n#\nset +x\necho \'# INSTALL SECTION\'\nset -x\nsome code\n' == generator.generateSection('install')
        generator.loadYamlString('language: ruby\ninstall:\n - some code\n - more code')
        assert '#\n# INSTALL SECTION\n#\nset +x\necho \'# INSTALL SECTION\'\nset -x\nsome code\nmore code\n' == generator.generateSection('install')
    }
    @Test public void test_lifecycleGenerator_generateAll() {
        generator.loadYamlString('language: ruby')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n\n#\n# INSTALL SECTION\n#\nset +x\necho \'# INSTALL SECTION\'\nset -x\nbundle install --jobs=3 --retry=3\n\n#\n# SCRIPT SECTION\n#\nset +x\necho \'# SCRIPT SECTION\'\nset -x\nbundle exec rake\n' == generator.generateAll()
    }
    @Test public void test_lifecycleGenerator_boolean_bug() {
        generator.loadYamlString('language: ruby\ninstall: true')
        assert '#\n# INSTALL SECTION\n#\nset +x\necho \'# INSTALL SECTION\'\nset -x\ntrue\n' == generator.generateSection('install')
        generator.loadYamlString('language: ruby\ninstall: [true,true]')
        assert '#\n# INSTALL SECTION\n#\nset +x\necho \'# INSTALL SECTION\'\nset -x\ntrue\ntrue\n' == generator.generateSection('install')
    }
    @Test public void test_lifecycleGenerator_isGenerateBranch() {
        generator.loadYamlString('language: ruby\nbranches:\n  only:\n    - master')
        assert true == generator.isGenerateBranch('master')
        assert false == generator.isGenerateBranch('development')
        generator.loadYamlString('language: ruby\nbranches:\n  except:\n    - master')
        assert false == generator.isGenerateBranch('master')
        assert true == generator.isGenerateBranch('development')
        generator.loadYamlString('language: ruby\nbranches:\n  only:\n    - development\n    - /^ma.*$/')
        assert true == generator.isGenerateBranch('master')
        assert true == generator.isGenerateBranch('development')
        generator.loadYamlString('language: ruby\nbranches:\n  except:\n    - development\n    - /^ma.*$/')
        assert false == generator.isGenerateBranch('many')
        assert false == generator.isGenerateBranch('development')
        generator.loadYamlString('language: ruby\nbranches:\n  - development\n  - /^ma.*$/')
        assert true == generator.isGenerateBranch('many')
        assert true == generator.isGenerateBranch('development')
        assert false == generator.isGenerateBranch('derp')
    }
    @Test public void test_lifecycleGenerator_setLabal_stability() {
        generator.label_stability = 'derp'
        assert 'stable'.equals(generator.label_stability)
        generator.label_stability = 'unstable'
        assert 'unstable'.equals(generator.label_stability)
        generator.label_stability = 'true'
        assert 'unstable'.equals(generator.label_stability)
        generator.label_stability = 'stable'
        assert 'stable'.equals(generator.label_stability)
    }
    @Test public void test_lifecycleGenerator_setLabal_sudo() {
        generator.label_sudo = 'derp'
        assert 'nosudo'.equals(generator.label_sudo)
        generator.label_sudo = 'nosudo'
        assert 'nosudo'.equals(generator.label_sudo)
        generator.label_sudo = 'true'
        assert 'sudo'.equals(generator.label_sudo)
        generator.label_sudo = 'sudo'
        assert 'sudo'.equals(generator.label_sudo)
        generator.label_sudo = 'required'
        assert 'sudo'.equals(generator.label_sudo)
    }
    @Test public void test_lifecycleGenerator_getObjectValue() {
        Map example = [key1: [subkey1: 'string']]
        assert 'string'.equals(generator.getObjectValue(example, 'key1.subkey1', 'default'))
        assert 'default'.equals(generator.getObjectValue(example, 'key2.subkey1', 'default'))
        assert 2.equals(generator.getObjectValue(example, 'key1.subkey1', 2))
    }
    @Test public void test_lifecycleGenerator_getObjectValue_false_bug() {
        Map example = [key1: [subkey1: 'false']]
        assert false == generator.getObjectValue(example, 'key1.subkey1', false)
        assert false == generator.getObjectValue(example, 'key1.subkey1', true)
    }
    @Test public void test_lifecycleGenerator_getObjectValue_type_bug() {
        Map example = [key1: [subkey1: 'string'],key2: ["a", "b"]]
        assert 'default'.equals(generator.getObjectValue(example, 'key1', 'default'))
        assert 'default'.equals(generator.getObjectValue(example, 'key2', 'default'))
    }
    @Test public void test_lifecycleGenerator_loadPlatforms() {
        assert null.equals(generator.platform_obj)
        URL url = this.getClass().getResource('/good_platforms_simple.json');
        generator.loadPlatforms(url.getFile())
        assert !null.equals(generator.platform_obj)
        assert generator.platform_obj.class == platformValidator
    }
    @Test public void test_lifecycleGenerator_loadPlatformsString() {
        assert null.equals(generator.platform_obj)
        URL url = this.getClass().getResource('/good_platforms_simple.json');
        String contents = new File(url.getFile()).getText()
        generator.loadPlatformsString(contents)
        assert !null.equals(generator.platform_obj)
        assert generator.platform_obj.class == platformValidator
    }
    @Test public void test_lifecycleGenerator_preloadYamlString() {
        String yaml = 'language: ruby'
        shouldFail(PlatformValidationException) {
            generator.preloadYamlString(yaml)
        }
        URL url = this.getClass().getResource('/good_platforms_simple.json');
        generator.loadPlatforms(url.getFile())
        generator.preloadYamlString(yaml)
        assert 'docker'.equals(generator.label_platform)
        assert 'ubuntu1404'.equals(generator.label_os)
        assert 'stable'.equals(generator.label_stability)
        assert 'sudo'.equals(generator.label_sudo)
    }
    @Test public void test_lifecycleGenerator_getLabels() {
        String yaml = 'language: ruby'
        generator.loadYamlString(yaml)
        assert 'language:ruby && gemfile && env && rvm && jdk'.equals(generator.getLabels())
        URL url = this.getClass().getResource('/good_platforms_simple.json');
        generator.loadPlatforms(url.getFile())
        generator.preloadYamlString(yaml)
        generator.loadYamlString(yaml)
        assert 'stable && docker && ubuntu1404 && sudo && language:ruby && gemfile && env && rvm && jdk'.equals(generator.getLabels())
    }
    @Test public void test_lifecycleGenerator_getLabels_additional_labels() {
        String yaml = 'language: ruby\njenkins:\n  additional_labels: foo'
        generator.loadYamlString(yaml)
        assert 'language:ruby && gemfile && env && rvm && jdk && foo'.equals(generator.getLabels())
        yaml = 'language: ruby\njenkins:\n  additional_labels:\n  - foo\n  - bar'
        generator.loadYamlString(yaml)
        assert 'language:ruby && gemfile && env && rvm && jdk && foo && bar'.equals(generator.getLabels())
    }
    @Test public void test_lifecycleGenerator_isRestricted() {
        String yaml = 'language: ruby'
        URL url = this.getClass().getResource('/good_platforms_simple.json');
        generator.loadPlatforms(url.getFile())
        generator.preloadYamlString(yaml)
        assert !generator.isRestricted('samrocketman/derp')
        assert !generator.isRestricted('samrocketman/jervis')
        assert generator.isRestricted('org/project')
    }
    @Test public void test_lifecycleGenerator_isSupportedPlatform() {
        assert true == generator.isSupportedPlatform()
        String yaml = 'language: ruby'
        URL url = this.getClass().getResource('/good_platforms_simple.json');
        generator.loadPlatforms(url.getFile())
        generator.preloadYamlString(yaml)
        generator.loadYamlString(yaml)
        assert true == generator.isSupportedPlatform()
        String tmp = generator.label_platform
        generator.label_platform = 'invalid'
        assert false == generator.isSupportedPlatform()
        generator.label_platform = tmp
        tmp = generator.label_os
        generator.label_os = 'invalid'
        assert false == generator.isSupportedPlatform()
        generator.label_os = tmp
        tmp = generator.yaml_language
        generator.yaml_language = 'invalid'
        assert false == generator.isSupportedPlatform()
        generator.yaml_language = tmp
        url = this.getClass().getResource('/good_platforms_optional.json');
        generator.loadPlatforms(url.getFile())
        generator.preloadYamlString(yaml)
        assert false == generator.isSupportedPlatform()
    }
    @Test public void test_lifecycleGenerator_setPrivateKey() {
        assert generator.secret_util == null
        URL url = this.getClass().getResource('/rsa_keys/good_id_rsa_2048');
        generator.setPrivateKey(url.content.text)
        assert generator.secret_util instanceof securityIO
        assert generator.secret_util.key_pair
    }
    @Test public void test_lifecycleGenerator_decryptSecrets_list() {
        URL url = this.getClass().getResource('/rsa_keys/good_id_rsa_2048');
        URL file_url = this.getClass().getResource('/rsa_keys/rsa_secure_properties_test.yml')
        generator.loadYamlString(file_url.content.text)
        shouldFail(SecurityException) {
            generator.decryptSecrets()
        }
        generator.setPrivateKey(url.content.text)
        assert generator.cipherlist
        assert !generator.plainlist
        generator.decryptSecrets()
        assert generator.plainlist
        assert generator.plainlist[0]['key'] == 'JERVIS_SECRETS_TEST'
        //decrypted plain text
        assert generator.plainlist[0]['secret'] == 'plaintext'
    }
    @Test public void test_lifecycleGenerator_decryptSecrets_map() {
        URL url = this.getClass().getResource('/rsa_keys/good_id_rsa_2048');
        URL file_url = this.getClass().getResource('/rsa_keys/rsa_secure_properties_map_test.yml')
        generator.loadYamlString(file_url.content.text)
        shouldFail(SecurityException) {
            generator.decryptSecrets()
        }
        generator.setPrivateKey(url.content.text)
        assert generator.ciphermap
        assert !generator.plainmap
        generator.decryptSecrets()
        assert generator.plainmap
        assert 'JERVIS_SECRETS_TEST' in generator.plainmap
        //decrypted plain text
        assert generator.plainmap['JERVIS_SECRETS_TEST'] == 'plaintext'
    }
    @Test public void test_lifecycleGenerator_decryptSecrets_list_to_map() {
        URL url = this.getClass().getResource('/rsa_keys/good_id_rsa_2048');
        URL file_url = this.getClass().getResource('/rsa_keys/rsa_secure_properties_test.yml')
        generator.loadYamlString(file_url.content.text)
        shouldFail(SecurityException) {
            generator.decryptSecrets()
        }
        generator.setPrivateKey(url.content.text)
        assert generator.ciphermap
        assert !generator.plainmap
        generator.decryptSecrets()
        assert generator.plainmap
        assert 'JERVIS_SECRETS_TEST' in generator.plainmap
        //decrypted plain text
        assert generator.plainmap['JERVIS_SECRETS_TEST'] == 'plaintext'
    }
    @Test public void test_lifecycleGenerator_decryptSecrets_map_to_list() {
        URL url = this.getClass().getResource('/rsa_keys/good_id_rsa_2048');
        URL file_url = this.getClass().getResource('/rsa_keys/rsa_secure_properties_map_test.yml')
        generator.loadYamlString(file_url.content.text)
        shouldFail(SecurityException) {
            generator.decryptSecrets()
        }
        generator.setPrivateKey(url.content.text)
        assert generator.cipherlist
        assert !generator.plainlist
        generator.decryptSecrets()
        assert generator.plainlist
        assert generator.plainlist[0]['key'] == 'JERVIS_SECRETS_TEST'
        //decrypted plain text
        assert generator.plainlist[0]['secret'] == 'plaintext'
    }
    @Test public void test_lifecycleGenerator_null_env_key() {
        //this should not generate an exception
        generator.loadYamlString('language: java\njdk:')
        generator.generateAll()
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#env toolchain section\n#jdk toolchain section\nsome commands\n'.equals(generator.generateToolchainSection())
    }
    @Test public void test_lifecycleGenerator_isInstanceFromList() {
        assert true == generator.isInstanceFromList("hello", [Number, String, List])
        assert true == generator.isInstanceFromList(3.5, [Number, String, List])
        assert true == generator.isInstanceFromList(3, [Number, String, List])
        assert true == generator.isInstanceFromList(['a', 'b'], [Number, String, List])
        assert false == generator.isInstanceFromList([:], [Number, String, List])
        assert true == generator.isInstanceFromList([:], [Number, String, Map])
    }
    @Test public void test_lifecycleGenerator_good_toolchains_disabled_env() {
        URL url = this.getClass().getResource('/good_toolchains_disabled_env.json');
        generator.loadToolchains(url.getFile())
        generator.loadYamlString('language: ruby\nenv:\n  - foo=bar\n  - hello=world')
        assert 'disabled' == generator.toolchain_obj.toolchainType('env')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\nexport foo=bar\nexport hello=world\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n' == generator.generateToolchainSection()
    }
    @Test public void test_lifecycleGenerator_good_lifecycles_python_number() {
        URL url = this.getClass().getResource('/good_lifecycles_python_number.json');
        generator.loadLifecycles(url.getFile())
        String compare_string = '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#env toolchain section\n#python toolchain section\nsome commands\n'
        generator.loadYamlString('language: python\npython: 2.7')
        assert compare_string == generator.generateToolchainSection()
        generator.loadYamlString('language: python\npython:\n  - 2.7')
        assert compare_string == generator.generateToolchainSection()
        generator.loadYamlString('language: python\npython: "2.7"')
        assert compare_string == generator.generateToolchainSection()
        generator.loadYamlString('language: python\npython:\n  - "2.7"')
        assert compare_string == generator.generateToolchainSection()
        generator.loadYamlString('language: python\npython:\n  global: 2.7')
        assert compare_string == generator.generateToolchainSection()
        generator.loadYamlString('language: python\npython:\n  global:\n    - 2.7')
        assert compare_string == generator.generateToolchainSection()
        generator.loadYamlString('language: python\npython:\n  matrix: 2.7')
        assert compare_string == generator.generateToolchainSection()
        generator.loadYamlString('language: python\npython:\n  matrix:\n    - 2.7')
        assert compare_string == generator.generateToolchainSection()
    }
    @Test public void test_lifecycleGenerator_bad_advanced_toolchain_when_not_supported() {
        URL url = this.getClass().getResource('/good_toolchains_disabled_env.json');
        generator.loadToolchains(url.getFile())
        generator.loadYamlString('language: ruby\nenv:\n  global:\n    - foo=bar\n    - hello=world')
        assert 'disabled' == generator.toolchain_obj.toolchainType('env')
        shouldFail(UnsupportedToolException) {
            generator.generateToolchainSection()
        }
    }
    @Test public void test_lifecycleGenerator_bad_null_entry() {
        generator.loadYamlString('language: shell\nscript:\n  - "some command"\n  -\n  - "another command"')
        assert '#\n# SCRIPT SECTION\n#\nset +x\necho \'# SCRIPT SECTION\'\nset -x\nsome command\n\nanother command\n' == generator.generateSection('script')
    }
    @Test public void test_lifecycleGenerator_bad_null_entry_with_false() {
        generator.loadYamlString('language: shell\nscript:\n  - "some command"\n  -\n  - false')
        assert '#\n# SCRIPT SECTION\n#\nset +x\necho \'# SCRIPT SECTION\'\nset -x\nsome command\n\nfalse\n' == generator.generateSection('script')
    }
    @Test public void test_lifecycleGenerator_good_additional_toolchains_dedup() {
        //should discard duplicate
        generator.loadYamlString('language: ruby\nadditional_toolchains: jdk')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n' == generator.generateToolchainSection()
        generator.loadYamlString('language: ruby\nadditional_toolchains:\n  - jdk')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n' == generator.generateToolchainSection()
        generator.loadYamlString('language: ruby\nadditional_toolchains:\n  - jdk\n  - compiler')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n#compiler toolchain section\nexport CXX="g++"\nexport CC="gcc"\n' == generator.generateToolchainSection()
    }
    @Test public void test_lifecycleGenerator_good_additional_toolchains_discard_nonexisting() {
        //discard non-existing additional toolchain
        generator.loadYamlString('language: ruby\nadditional_toolchains: foo')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n' == generator.generateToolchainSection()
        generator.loadYamlString('language: ruby\nadditional_toolchains:\n  - foo')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n' == generator.generateToolchainSection()
        generator.loadYamlString('language: ruby\nadditional_toolchains:\n  - foo\n  - compiler')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n#compiler toolchain section\nexport CXX="g++"\nexport CC="gcc"\n' == generator.generateToolchainSection()
    }
    @Test public void test_lifecycleGenerator_good_additional_toolchains_inline() {
        generator.loadYamlString('language: ruby\ncompiler: gcc')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n#compiler toolchain section\nexport CXX="g++"\nexport CC="gcc"\n' == generator.generateToolchainSection()
        generator.loadYamlString('language: ruby\ncompiler: clang')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n#compiler toolchain section\nexport CXX="clang++"\nexport CC="clang"\n' == generator.generateToolchainSection()
    }
    @Test public void test_lifecycleGenerator_good_additional_toolchains_ordered() {
        generator.loadYamlString('language: ruby\nadditional_toolchains:\n  - python\n  - compiler')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n#python toolchain section\nsome commands\n#compiler toolchain section\nexport CXX="g++"\nexport CC="gcc"\n' == generator.generateToolchainSection()
        //reset toolchains for reverse order
        URL url = this.getClass().getResource('/good_toolchains_simple.json');
        generator.loadToolchains(url.getFile())
        //test reverse order
        generator.loadYamlString('language: ruby\nadditional_toolchains:\n  - compiler\n  - python')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n#compiler toolchain section\nexport CXX="g++"\nexport CC="gcc"\n#python toolchain section\nsome commands\n' == generator.generateToolchainSection()
    }
    @Test public void test_lifecycleGenerator_good_additional_toolchains_inline_ordered() {
        generator.loadYamlString('language: ruby\npython: "2.7"\ncompiler: gcc')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n#python toolchain section\nsome commands\n#compiler toolchain section\nexport CXX="g++"\nexport CC="gcc"\n' == generator.generateToolchainSection()
        //reset toolchains for reverse order
        URL url = this.getClass().getResource('/good_toolchains_simple.json');
        generator.loadToolchains(url.getFile())
        //test reverse order
        generator.loadYamlString('language: ruby\ncompiler: gcc\npython: "2.7"')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n#compiler toolchain section\nexport CXX="g++"\nexport CC="gcc"\n#python toolchain section\nsome commands\n' == generator.generateToolchainSection()
    }
    @Test public void test_lifecycleGenerator_good_additional_toolchains_string() {
        generator.loadYamlString('language: ruby\nadditional_toolchains: compiler')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n#compiler toolchain section\nexport CXX="g++"\nexport CC="gcc"\n' == generator.generateToolchainSection()
    }
    @Test public void test_lifecycleGenerator_good_toolchains_cleanup() {
        URL url = this.getClass().getResource('/good_toolchains_cleanup.json');
        generator.loadToolchains(url.getFile())
        generator.loadYamlString('language: ruby')
        assert '#\n# TOOLCHAINS SECTION\n#\nset +x\necho \'# TOOLCHAINS SECTION\'\nset -x\n#gemfile toolchain section\nexport BUNDLE_GEMFILE="${PWD}/Gemfile"\n#env toolchain section\n#rvm toolchain section\nsome commands\n#jdk toolchain section\nsome commands\n#cleanup toolchain section\nfunction rvm_cleanup_on() {\n  set +x\n  some cleanup command\n}\ntrap rvm_cleanup_on EXIT\n' == generator.generateToolchainSection()
    }
    @Test public void test_lifecycleGenerator_serialization() {
        URL url = this.getClass().getResource('/good_platforms_simple.json');
        generator.loadPlatforms(url.getFile())
        new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(generator)
    }
    @Test public void test_lifecycleGenerator_matrix_fullName_by_friendly() {
        generator.loadYamlString('language: ruby\nenv:\n  - foo=hello\n  - bar=world\n  - baz=goodbye')
        //generator.generateToolchainSection()
        assert true == generator.isMatrixBuild()
        assert generator.matrix_fullName_by_friendly['env0'] == 'env:foo=hello'
        assert generator.matrix_fullName_by_friendly['env1'] == 'env:bar=world'
        assert generator.matrix_fullName_by_friendly['env2'] == 'env:baz=goodbye'
        generator.loadYamlString('language: ruby\nenv:\n  matrix:\n    - foobar=foo\n    - foobar=bar')
        assert true == generator.isMatrixBuild()
        assert generator.matrix_fullName_by_friendly['env0'] == 'env:foobar=foo'
        assert generator.matrix_fullName_by_friendly['env1'] == 'env:foobar=bar'
        assert generator.matrix_fullName_by_friendly.get('env2', 'is empty') == 'is empty'
        generator.loadYamlString('language: ruby')
        assert false == generator.isMatrixBuild()
        assert !generator.matrix_fullName_by_friendly
    }
    @Test public void test_lifecycleGenerator_isPipelineJob() {
        generator.loadYamlString('language: ruby\njenkins:\n  pipeline_jenkinsfile: path/Jenkinsfile')
        assert true == generator.isPipelineJob()
        generator.loadYamlString('language: ruby')
        assert false == generator.isPipelineJob()
        generator.folder_listing = ['Jenkinsfile']
        assert true == generator.isPipelineJob()
    }
    @Test public void test_lifecycleGenerator_getJenkinsfile() {
        generator.loadYamlString('language: ruby')
        assert 'Jenkinsfile' == generator.getJenkinsfile()
        generator.loadYamlString('language: ruby\njenkins:\n  pipeline_jenkinsfile: path/Jenkinsfile')
        assert 'path/Jenkinsfile' == generator.getJenkinsfile()
    }
}
