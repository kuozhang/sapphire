### Introduction


[Sapphire](http://www.eclipse.org/sapphire/) is a user interface development framework that improves productivity.
Instead of focusing on individual widgets, layouts and data binding, the developers
focus on modeling the semantics of the data and declaring the general intent of
how the data it to be presented.

[More Information](http://www.eclipse.org/sapphire/documentation/introduction)
 
### License

[Eclipse Public License (EPL)](http://www.eclipse.org/legal/epl-v10.html)

### Releases

Information on past and future releases along with the downloads can be found on 
[the releases page](http://www.eclipse.org/sapphire/releases/).
 
### Discussion
 
Questions should be directed to [the adopter forum](http://www.eclipse.org/forums/index.php/f/192/).

### Bugzilla
 
This project uses [Bugzilla](https://bugs.eclipse.org/bugs/report.cgi?x_axis_field=bug_status&y_axis_field=bug_severity&z_axis_field=&no_redirect=1&query_format=report-table&short_desc_type=allwordssubstr&short_desc=&classification=Technology&product=Sapphire&resolution=---&resolution=FIXED&longdesc_type=allwordssubstr&longdesc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&status_whiteboard_type=allwordssubstr&status_whiteboard=&keywords_type=allwords&keywords=&bug_id=&bug_id_type=anyexact&votes=&votes_type=greaterthaneq&emailtype1=substring&email1=&emailtype2=substring&email2=&emailtype3=substring&email3=&chfieldvalue=&chfieldfrom=&chfieldto=Now&j_top=AND&f1=noop&o1=noop&v1=&format=table&action=wrap)
to track issues and enhancement requests.
 
 * [Search](https://bugs.eclipse.org/bugs/query.cgi?classification=Technology&product=Sapphire)
 * [Report Problems or Request Enhancements](https://bugs.eclipse.org/bugs/enter_bug.cgi?classification=Technology&product=Sapphire)
 
### Installing

Sapphire is distributed as a p2 repository, from which the various components can be installed. Every [release page](http://www.eclipse.org/sapphire/releases/)
either lists the URL of the repository (for finished releases) or includes a link to a Hudson build job (for in-progress releases).

[Releases](http://www.eclipse.org/sapphire/releases/)
 
### Building

 1. Make sure that you have JDK 6 and JDK 7 and Ant installed. All should be on the path.
 2. Set `JDK_6_HOME` environment variable to point to your JDK 6 install.
 3. Set `JDK_7_HOME` environment variable to point to your JDK 7 install.
 4. Clone the Sapphire Git repository and pick the 8.2.x branch.
 5. Open a shell to the Git workspace and execute `ant clean-start -buildfile build-csp.xml`.
 
 Once the build completes, you will notice the following key folders in the Git workspace:

 * **build/repository** : Repository of build artifacts, including runtime bundles, source bundles and the SDK.

### Contributing

Contributions to this project are always welcome as a Bugzilla attachment of a patch. This project currently does not
accept contributions through Gerrit or through GitHub pull requests.

Note that before your contribution can be accepted, you need to
complete the [Contributor License Agreement (CLA)](http://www.eclipse.org/legal/CLA.php). 
See [FAQ](https://www.eclipse.org/legal/clafaq.php) for more information.
