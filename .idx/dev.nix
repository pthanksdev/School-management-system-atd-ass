# To learn more about how to use Nix to configure your environment
# see: https://firebase.google.com/docs/studio/customize-workspace
{ pkgs, ... }: {
  # Which nixpkgs channel to use.
  channel = "stable-24.05"; 

  # Use https://search.nixos.org/packages to find packages
  packages = [
    pkgs.jdk21
    pkgs.maven
    pkgs.kubectl
    pkgs.kind
    
    # 1. ADD POSTGRESQL CLI TOOLS
    # This gives you the 'psql' command in your terminal to inspect your DB
    pkgs.postgresql
  ];

  # ENABLE DOCKER DAEMON
  services.docker.enable = true;

  # Sets environment variables in the workspace
  env = {};
  
  idx = {
    # Search for the extensions you want on https://open-vsx.org/ and use "publisher.id"
    extensions = [
      "vscjava.vscode-java-pack"
      "ms-azuretools.vscode-docker"
      "ms-kubernetes-tools.vscode-kubernetes-tools"
      
      # 2. ADD SPRING BOOT EXTENSION
      # Gives you smart code-completion for application.properties and Spring annotations
      "vmware.vscode-spring-boot" 
    ];

    # Enable previews
    previews = {
      enable = true;
      previews = {};
    };

    # Workspace lifecycle hooks
    workspace = {
      onCreate = {};
      onStart = {};
    };
  };
}