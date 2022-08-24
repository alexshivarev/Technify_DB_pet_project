package technify.business;

public class Song {

    int id = -1;
    String name = null;
    String genre = null;
    String country = null;
    int playCount = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getPlayCount() {
        return playCount;
    }
    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public static Song badSong()
    {
        return new Song();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;

        Song song = (Song) o;

        if (getId() != song.getId()) return false;
        if (getName() != null ? !getName().equals(song.getName()) : song.getName() != null) return false;
        if (getGenre() != null ? !getGenre().equals(song.getGenre()) : song.getGenre() != null) return false;
        if (getPlayCount() != song.getPlayCount()) return false;
        return getCountry() != null ? getCountry().equals(song.getCountry()) : song.getCountry() == null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Song{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", genre='").append(genre).append('\'');
        sb.append(", country='").append(country).append('\'');
        sb.append(", playCount='").append(playCount).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
