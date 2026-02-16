package club.revived.commons.feature;

import java.util.ArrayList;
import java.util.List;

public final class FeatureRegistry {

  private final List<Feature> features = new ArrayList<>();

  public FeatureRegistry() {
  }

  public boolean isRegistered(final Class<? extends Feature> clazz) {
    return features.stream().anyMatch(feature -> feature.getClass().equals(clazz));
  }

  public void register(final Feature feature) {
    if (isRegistered(feature.getClass())) {
      throw new IllegalStateException("Feature " + feature.getClass().getSimpleName() + " is already registered.");
    }

    features.add(feature);
  }

  public void enableFeature(final Feature feature) {
    feature.onEnable();

    this.features.add(feature);
  }

  public void init() {
    features.forEach(Feature::onEnable);
  }
}
