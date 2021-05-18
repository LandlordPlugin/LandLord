import org.gradle.api.Project

class PublishData(val project: Project) {

    /**
     * function to provide the current commit hash provided via github actions
     *
     * @return commit hash or local if not executed by github actions.
     */
    private fun getCheckedOutGitCommitHash(): String {
        val takeFromHash = 7
        val commit = System.getenv("GITHUB_SHA")
        if (commit != null) {
            // We are running in an github action.
            return commit.substring(0, takeFromHash)
        }
        return "local"
    }

    /**
     * function to provide the current ref provided via github actions.
     *
     * @return commit hash or local if not executed by github actions.
     */
    private fun getCheckedOutBranch(): String {
        val branch = System.getenv("GITHUB_REF")
        if (branch != null) {
            return branch.replace("refs/heads/", "")
        }
        return "local"
    }

    /**
     * Get the publish data
     *
     * @return version as string and boolean which indicated whether this is a production build or not
     */
    fun getVersion(): String? {
        val branch = getCheckedOutBranch()
        println("Current branch is $branch")
        var currVer = project.version as String?
        currVer = when {
            branch.contentEquals("master") -> {
                println("Project is PROD version")
                // remove snapshot for production builds
                currVer?.replace("-SNAPSHOT", "")
            }
            branch.startsWith("develop") -> {
                println("Project is DEV version")
                currVer?.replace("-SNAPSHOT", "").plus("-dev-" + getCheckedOutGitCommitHash())
            }
            else -> {
                println("Project is SNAPSHOT")
                (if (currVer?.endsWith("-SNAPSHOT") == true) "" else currVer?.plus("-SNAPSHOT")).plus("-").plus(getCheckedOutGitCommitHash())
            }
        }
        println("Current project version is $currVer")
        return currVer
    }

    fun isSnapshot(): Boolean {
        return !getCheckedOutBranch().contentEquals("master")
    }
}