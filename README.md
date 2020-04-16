<div style="display: flex; align-items: center">
    <img src="https://i.imgur.com/2hb1EXu.png" width="100">
    <div style="margin-left: 20px">
        <h1 style="margin: 0; padding: 0">Rhein</h1>
        <p style="margin: 0">FRP Data Propagation Library</p>
    </div>
</div>

Rhein is a data-propagation library based on Functional Reactive Programming abstractions such as Events and Behaviours that helps you to develop interactive applications using a conceptual-declarative approach that brings numerous benefits to the quality of the appli- cations and also solves several problems the mainstream methods of development of this type of software produce.


## Requirements
The following are required:
- Scala 2.12.8
- sbt 1.3.2

Rhein uses these dependencies which are already specified in the `build.sbt` file:
- scala-js 0.6.31
- scalajs-dom 0.9.7
- scalatags 0.7.0

## Instructions
>**Note!** The package is not deployed to any dependency management server. You will have to add the `rhein` folder inside your project to use Rhein.

1. After you've successfully installed sbt on your machine, go to the root directory of the project and run `sbt`. This process takes a bit longer for the first run, because it downloads all dependencies.

2. After you have sucessfully launeched the build tool, you now have to choose what you wnat to do. You can either compile the current examples using `fastOptJS` which generates a javascript file in `target/scala-2.12/rhein-fastopt.js`. You can add this file in a HTML file, but we already provided a file that has this file linked in `src/main/resources/index-opt.html`. To run, open this file in a browser.

3. If you decide to create new code, you must import the package using `import rhein._`. This imports all FRP abstractions available in Rhein. To import the UI Binding library use `import rhein.ui._`. PS! If you want to use the Bindings from the UI Biding Library, you neeed to import this file in your class using `import Bindings._` (make sure you have the ui package imported first).

4. If you want to render elements in the browser, you must import scalatags and scalajs. The main entry of the program is the `Main.scala` class. You can change this to any class you want, but make sure you specify which is the main entry in the compiled javascript using `@JSExportTopLevel("Main")`. 

5. To run tests, type `testOnly` in your sbt.

For more information about scalatags, check [this](https://www.scala-js.org/). And For information about ScalaJS check [this](https://www.lihaoyi.com/scalatags/).

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
