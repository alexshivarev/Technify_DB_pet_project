package technify.business;

public class Playlist {

    int id = -1;
    String genre = null;
    String description = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Playlist badPlaylist()
    {
        return new Playlist();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Playlist)) return false;

        Playlist playlist = (Playlist) o;

        if (getId() != playlist.getId()) return false;
        if (getGenre() != null ? !getGenre().equals(playlist.getGenre()) : playlist.getGenre() != null) return false;
        return getDescription() != null ? getDescription().equals(playlist.getDescription()) : playlist.getDescription() == null;
    }
//
//    @Override
//    public int hashCode() {
//        int result = getId();
//        result = 31 * result + (getGenre() != null ? getGenre().hashCode() : 0);
//        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
//        return result;
//    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Playlist{");
        sb.append("id=").append(id);
        sb.append(",genre='").append(genre).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
