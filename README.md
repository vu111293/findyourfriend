
GitHub for beginners (debian/ubuntu/linux)
Posted on February 15, 2013 by chirag1992m	

Just yesterday I was trying to setup my first github repository, but it wasn’t that easy I thought it would be. YES I know, github has a support page, but  it didn’t seemed to work for me.

So here, I’ll give you a little introduction on the initial setup of git on your local machine and setting up of your cloud repository on github. This tutorial is for debian/ubuntu users.

GIT
Git is an extremely fast, efficient, distributed version control system ideal for the collaborative development of software.

GITHUB
GitHub is the best way to collaborate with others. Fork, send pull requests and manage all your public and private git repositories. In simple terms, it is a cloud storage for all your repositories, be it local or distributed.

SETTING UP OF GIT REPOSITORY ON LOCAL MACHINE
NOTE: GIT CAN BE USED WITH/WITHOUT GITHUB. GIT is a subversion control system which can just be used solely on your local machine without using github at all.

First we’ll setup git on the local machine and create a local repository.
Install GIT using the command on your terminal:
1	sudo apt-get install git

This will install git on your machine. Now you we’ll start by doing a bit of configuration. Every commit you make will have your name and email address to identify the ‘owner’ of the commit, so you should start by giving it those values. To do so, run these commands:
1	git config --global user.name "your name"
2	git config --global user.email "your@email.com"

So Git knows who you are now. Let’s work upon the local repository we were talking about,  just imagine you are creating a simple PHP web app. (Of course, the bigger the project, the brighter Git shines, but we’re just learning the tools, right?) We’ve got an empty directory called ‘project’. Add all your project files in the new created empty directory. This is the base directory for your repository. To get started with your very own repository, first change your current working directory to the base repository directory and initialize git. Type:
1	cd /path/to/project/
2	touch README.md
3	git init
4	git add *
5	git commit -m "commit message"
6	git status

Let me explain each command I typed:

    cd /path/to/project/: This just changes your current working directory to the project directory for setting up of repository.
    touch README.md: README.md is a file used by github as a default readme file. So we create an empty file. This step is not necessary, but it surely makes you ready for uploading your repository on github.
    git init: this initializes your git repository by making a “.git” directory as a sub-directory to your project directory. This is the magic black box used by git where all the different branches, logs and commits are kept.
    git add *: This adds all the files to be committed to a staging area. The staging area is a spot to hold files for your next commit; maybe you don’t want to commit all your current changes, so you put some in the staging area. “*” is used to add all the files. You can be specific in adding files. Just use ‘*.js’ to only add javascript files.
    you can remove files from the staging area and subsequently from your commit using the command: git rm file_name
    git commit -m “commit message”: It finally commits the staged files and logs the given commit message for you to recognize your commit later on when you want to rollback or branch.
    For skipping the staging area and updating all the already committed files use:

    1	git commit -am "commit updated"
    git status: The git status command allows you to see the current state of your code. We’ve just done a commit, so git status will show us that there’s nothing new. If you continue working on your imaginary project, you’ll see that the status changes. If you have any new files, it will show you the files under “untracked files.” or if you have made changes to the files already committed, it will show you the files under “changed but not updated”. Again running the add and commit commands will add these new/changed files to a new commit.

You can checkout all the available commands/features on git here: http://git-scm.com/book/commands

you can similarly setup more repositories on your local machine. :) 
