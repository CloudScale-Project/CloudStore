from cloudscale.deployment_scripts.scripts import read_config, create_user_path


class Config:

    def __init__(self, output_directory, config_path):
        self.config_path = config_path
        self.user_path = create_user_path(output_directory)
        self.cfg = read_config(self.config_path)
        self.provider = self.cfg.get('COMMON', 'provider')
        self.db_provider = self.cfg.get('COMMON', 'db_provider')

    def save(self, section, variable, value):
        self.cfg.save_option(self.config_path, section, variable, str(value))

