public enum Language {
    TR("Türkçe"), EN("English"), IT("Italiano"), FR("Français"), DE("Deutsch");
    public final String nativeName;
    Language(String nativeName) { this.nativeName = nativeName; }
    @Override public String toString() { return nativeName; }
}
