from form.models import Ec2InstanceTypes, RDSInstanceTypes


class Validate:

    def flavor(self, data):
        errors = []
        if data['fr']['own_infrastructure'] == 'yes':
            if data['fr']['auth_url'] == None:
                errors.append("You need to provide authentication url for OpenStack")
            if data['fr']['username'] == None:
                errors.append("You need to provide username for OpenStack")
            if data['fr']['password'] == None:
                errors.append("You need to provide password for OpenStack")
            if data['fr']['tenant'] == None:
                errors.append("You need to provide tenant for OpenStack")
        elif data['fr']['own_infrastructure'] == None:
            errors.append("You need to select which OpenStack infrastructure you want to use")
        return errors

    def finish(self, data):
        errors = []
        if data.get('provider') == None:
            errors.append('You need to choose provider at <a href="#/step2">step 2</a>!')

        if data.get('db_provider') == None:
            errors.append('You need to choose database provider! at <a href="#/step2">step 2</a>!')

        if data.get('provider') == 'aws':
            if data.get('fr').get('access_key') == None:
                errors.append('You need to provide access key at <a href="#/step3">step 3</a>!')

            if data.get('fr').get('secret_key') == None:
                errors.append('You need to provide secret key at <a href="#/step3">step 3</a>!')

            if data.get('fr').get('autoscaling') == None:
                errors.append('You need to choose if you want autoscaling or not at <a href="#/step3">step 3</a>!')

            if data.get('fr').get('autoscaling') == 'no' and data.get('fr').get('num_instances') == None:
                errors.append(
                    'You chose that you don\'t want autoscaling but didn\'t choose how many instances do you want at <a href="#/step3">step 3</a>')

            if data.get('fr').get('instance_type') not in [i.instance_type for i in Ec2InstanceTypes.objects.all()]:
                errors.append('You need to choose a valid instance type at <a href="#/step3">step 3</a>!')

            if data.get('db_provider') == 'mysql':
                if data.get('db').get('num_instances') == None:
                    errors.append('You need to choose the number of database instances at <a href="#/step4">step 4</a>')

                if data.get('db').get('connection_pool_size') == None:
                    errors.append('You need to specify the size of connection pool at <a href="#/step4">step 4</a>')

                if data.get('db').get('instance_type') not in [i.instance_type for i in RDSInstanceTypes.objects.all()]:
                    errors.append('You need to choose the instance type at <a href="#/step4">step 4</a>!')
            else:
                errors.append("You chose the AWS provider, and only supported database with that provider is MySQL!")

        if data.get('provider') == 'openstack':
            # TODO: validate openstack form
            pass

        return errors
