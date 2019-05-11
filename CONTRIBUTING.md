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
    
Steps
-----

1. Clone the repository 
2. Execute setup.sh for installing the testservers with all dependencies
3. Testserver is available in the target folders (excluded from git)
4. Change code while respecting above conventions
5. Submit a pull-request