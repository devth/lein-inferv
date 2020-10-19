# lein-inferv

`lein-inferv` is a highly opinionated way of inferring version from the current
date, time, and git version for
[Leiningen](https://github.com/technomancy/leiningen) projects.

## Configuration

There is no configuration.

## Usage

1. In `project.clj` set the version to `"_"` (this is not technically necessary,
   but a good indicator that the standard version in Leiningen is not used)
1. Add to the `:plugins` vector in `project.clj`:

   ```clojure
   [lein-inferv "LATEST"]
   ```

1. Test it out, e.g. using [`lein
   pprint`](https://github.com/technomancy/leiningen/tree/master/lein-pprint):

   ```bash
   lein pprint
   ```

Now the `:version` used by lein will be inferred in format `x.y.z` where:

- `x` is the current date in UTC
- `y` is the current time in UTC
- `z` is the short git sha of the latest commit

## License

Copyright © 2020 Trevor Hartman

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
