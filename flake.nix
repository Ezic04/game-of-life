{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs =
    { nixpkgs, flake-utils, ... }:
    flake-utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
        build-sim = pkgs.writeShellScriptBin "build-sim" ''
          mvn clean compile exec:java
        '';
      in
      {
        devShells.default = pkgs.mkShell {
          nativeBuildInputs = with pkgs; [
            jdk25
            maven
            build-sim
          ];
          shellHook = ''
            export JAVA_HOME=${pkgs.jdk25}/lib/openjdk
            export PATH=$JAVA_HOME/bin:$PATH

            mkdir -p .vscode
            cat > .vscode/settings.json <<EOF
            {
                "java.format.enabled": true,
                "java.format.settings.url": "eclipse-formatter.xml",
                "java.jdt.ls.java.home": "$JAVA_HOME",
                "[java]": {
                    "editor.defaultFormatter": "redhat.java",
                    "editor.formatOnSave": true
                }
            }
            EOF
          '';
        };
      }
    );
}
