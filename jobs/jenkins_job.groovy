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

//this code should be at the beginning of every script included which requires bindings
String include_script_name = 'jobs/jenkins_job.groovy'
Set required_bindings = ['parent_job', 'project', 'project_folder', 'project_name', 'script_approval', 'git_service', 'jenkinsJobClassic', 'jenkinsJobPipeline']
Set missing_bindings = required_bindings - (binding.variables.keySet()*.toString() as Set)
if(missing_bindings) {
    throw new Exception("${include_script_name} is missing required bindings from calling script: ${missing_bindings.join(', ')}")
}

/*
   Configures matrix or freestyle jobs for both main and pull request builds.
 */

import net.gleske.jervis.lang.lifecycleGenerator
import static net.gleske.jervis.lang.lifecycleGenerator.getObjectValue

jenkinsJob = null
jenkinsJob = { lifecycleGenerator generator, boolean isPullRequestJob, String JERVIS_BRANCH ->
    boolean pipelineDefault = '.jervis.yml' in generator.folder_listing
    //chooses job type based on Jervis YAML
    def jervis_jobType
    if(pipeline_jenkinsfile) {
        println "Generating branch:\n    ${JERVIS_BRANCH.split().join('\n    ')}"
        //pipeline job instead of multi-branch
        //jervis_jobType = { String name, Closure closure -> parent_job.pipelineJob(name, closure) }
        //jenkinsJobPipeline(jervis_jobType, generator, JERVIS_BRANCH)
        jervis_jobType = { String name, Closure closure -> parent_job.multibranchPipelineJob(name, closure) }
        jenkinsJobMultibranchPipeline(jervis_jobType, JERVIS_BRANCH)
    }
    else {
        println "Generating branch: ${JERVIS_BRANCH}"
        //use classic job type
        if(generator.isMatrixBuild()) {
            jervis_jobType = { String name, Closure closure -> parent_job.matrixJob(name, closure) }
        }
        else {
            jervis_jobType = { String name, Closure closure -> parent_job.freeStyleJob(name, closure) }
        }
        jenkinsJobClassic(jervis_jobType, generator, isPullRequestJob, JERVIS_BRANCH)
    }
}
