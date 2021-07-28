Contributing
========

Thanks for your interest in contributing code for landlord. Please read following guidelines carefully, as your code
might not get approved, if there are major problems.

- We are using IntelliJs autoformat feature. In addition to that there are certain requirements:
    * Please do not leave out curly braces in one liner!
    * Please do not use enters in front of curly braces!
- Wrap code to 120 column limit (standard in intellij)
- Always comment your code.
- Do not put multiple changes into one commit!
- You may want to create feature branches for larger projects.
- Discuss larger changes with the team before working for nothing
- Please make sure to pull termination conditions to the front:  
  **bad:**
    ```
    if(land.isFriend(uuid)) {
        if(weirdList.contains(uuid)) {
            if (this.than == that) {
                // ...
            }
        }
    }
    ```
  **good:**
    ```
    if (!land.isFriend(uuid)) {
        return;
    }
    if (!weirdList.contains(uuid)) {
        return;        
    }
    if (this.than != that) {
        return;
    }
    // ...
    ```

# Pull Requests
When you create a new pull request you have to take care of a few things.

- Create a feature branch based on the development branch
- Give your feature branch a reasonable name
- Make your changes
- Test your changes and features.
- Make sure your feature branch is rebased on the latest commit on development
- Open a PR
- Set development as a target
- Describe what and why you changed it.
- Submit your PR
- Keep an eye if any changes are requested or if you receive any comments.
- (Apply changes to your PR)
- You PR gets merged or denied.

Thank you for your contribution!
