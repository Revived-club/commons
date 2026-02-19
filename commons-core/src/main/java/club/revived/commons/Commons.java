package club.revived.commons;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.data.DataRepository;
import club.revived.commons.data.DatabaseType;
import club.revived.commons.data.model.DatabaseCredentials;
import club.revived.commons.distribution.Cluster;
import club.revived.commons.distribution.kvbus.providers.broker.RedisBroker;
import club.revived.commons.distribution.kvbus.providers.cache.RedisCacheStore;
import club.revived.commons.distribution.service.ServiceType;
import club.revived.commons.shared.SharedDataManager;

public abstract class Commons {

  protected Cluster cluster;

  public void init(final @NotNull ServiceType serviceType) {
    this.initDataRepository();
    this.initCluster(serviceType);
    this.cluster.init();
    this.initMessageHandlers();
    this.initHeartbeats();
  }

  private void initDataRepository() {
    final var repository = new DataRepository(DatabaseType.MONGODB);

    repository.init(this.getMongoDBCredentials());
    repository.initLogging(this.getInfluxDBCredentials(), DatabaseType.INFLUXDB);

    SharedDataManager.getInstance().initSharedData();
  }

  @NotNull
  private DatabaseCredentials getMongoDBCredentials() {
    final String host = System.getenv("MONGODB_HOST");
    final String password = System.getenv("MONGODB_PASSWORD");
    final String username = System.getenv("MONGODB_USERNAME");
    final String database = System.getenv("MONGODB_DATABASE");

    return new DatabaseCredentials(username, host, password, 27017, database);
  }

  @NotNull
  private DatabaseCredentials getInfluxDBCredentials() {
    final String host = System.getenv("INFLUXDB_HOST");
    final String password = System.getenv("INFLUX_TOKEN");
    final String username = System.getenv("INFLUXDB_ORG");
    final String database = System.getenv("INFLUXDB_BUCKET");

    return new DatabaseCredentials(username, host, password, 8086, database);
  }

  private void initCluster(final @NotNull ServiceType serviceType) {
    final String hostName = System.getenv("HOSTNAME");
    final String host = System.getenv("REDIS_HOST");
    final int port = Integer.parseInt(System.getenv("REDIS_PORT"));

    this.cluster = new Cluster(
        new RedisBroker(host, port, ""),
        new RedisCacheStore(host, port, ""),
        serviceType,
        this.getServiceIP(),
        hostName);
  }

  protected abstract String getServiceIP();

  protected abstract void initMessageHandlers();

  protected abstract void initHeartbeats();

}
