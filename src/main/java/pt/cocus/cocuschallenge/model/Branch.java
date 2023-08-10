package pt.cocus.cocuschallenge.model;

public record Branch(String name, String sha) {
    public Branch(String name, String sha) {
        this.name = name;
        this.sha = sha;
    }
}
