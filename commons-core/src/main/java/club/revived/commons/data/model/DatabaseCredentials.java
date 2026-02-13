package club.revived.commons.data.model;

public record DatabaseCredentials(String user, String host, String password, int port, String database) {
}
