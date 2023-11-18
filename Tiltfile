load('ext://namespace', 'namespace_create', 'namespace_inject')

settings = read_json('tilt-settings.json', default={})

namespace= "tax-calculator"

if settings.get("allow_k8s_contexts"):
  allow_k8s_contexts(settings.get("allow_k8s_contexts").format(namespace))

if settings.get("default_registry"):
  default_registry(settings.get("default_registry").format(namespace))

docker_build('tax-calculator-image',
             '.')

namespace_create(namespace)

yaml=helm(
         './helm',
         name='tax-calculator',
         namespace=namespace,
         values=['values-local.yaml'])

k8s_yaml(yaml)