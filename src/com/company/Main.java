package com.company;

import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
public class Main {

    private static Scanner scan = new Scanner(System.in);
    static AmazonEC2 ec2;
    public static void main(String[] args) {
        int selectmenu=0;
        while(true){
            System.out.println("----------------------------------------");
            System.out.println("1. list instance        2. available zones");
            System.out.println("3. start instance       4. available regions");
            System.out.println("5. stop instance        6. create instance");
            System.out.println("7. reboot instance      8. list images");
            System.out.println("                        99. quit");
            System.out.println("----------------------------------------");
            System.out.print("=>");
            selectmenu = scan.nextInt();
            scan.nextLine();
            if(selectmenu==99) break;
            Menu(selectmenu);
            System.out.println();

        }

    }

    private static void Menu(int selectmenu){
        String instanceId = null;
        String amiId = null;

        switch(selectmenu){
            case 1:
                System.out.println("Instances List--");
                listInstance();
                break;
            case 2:
                System.out.println("Available Zones---");
                availableZones();
                break;
            case 3:
                System.out.print("Enter Instance ID:");
                instanceId = scan.nextLine();
                startInstance(instanceId);
                System.out.println("Instance Started");
                break;
            case 4:
                System.out.println("Available Regions---");
                availableRegions();
                break;
            case 5:
                System.out.print("Enter Instance ID:");
                instanceId = scan.nextLine();
                stopInstance(instanceId);
                System.out.println("Instance Stopped");
                break;
            case 6:
                System.out.print("Enter Instance AMI_ID:");
                amiId = scan.nextLine();
                createInstance(amiId);
                System.out.println("Instance created");
                break;
            case 7:
                System.out.print("Enter Instance ID:");
                instanceId = scan.nextLine();
                rebootInstance(instanceId);
                System.out.println("Instance Rebooted");
                break;
            case 8:
                System.out.println("Images List---");
                listImage();
                break;
            default:
                System.out.println("Please Enter number");
                break;
        }
    }
    public static void createInstance(String ami_id){
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        ec2 = AmazonEC2ClientBuilder
                .standard()
                .withCredentials(credentialsProvider)
                .withRegion("ap-northeast-2")
                .build();

        RunInstancesRequest run_request = new RunInstancesRequest()
                .withImageId(ami_id)
                .withInstanceType(InstanceType.T2Micro)
                .withMaxCount(1)
                .withMinCount(1);

        RunInstancesResult run_response = ec2.runInstances(run_request);

        String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();
    }
    public static void startInstance(String instance_id){
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.startInstances(request);
    }

    public static void stopInstance(String instance_id){
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        StopInstancesRequest request = new StopInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.stopInstances(request);
    }
    public static void rebootInstance(String instance_id){
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        RebootInstancesRequest request = new RebootInstancesRequest()
                .withInstanceIds(instance_id);

        RebootInstancesResult response = ec2.rebootInstances(request);
    }
    public static void availableRegions(){
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        DescribeRegionsResult regions_response = ec2.describeRegions();

        for(Region region : regions_response.getRegions()) {
            System.out.printf(
                    "Found region %s " +
                            "with endpoint %s",
                    region.getRegionName(),
                    region.getEndpoint());
            System.out.println();
        }
    }

    public static void availableZones(){
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        DescribeAvailabilityZonesResult zones_response =
                ec2.describeAvailabilityZones();

        for(AvailabilityZone zone : zones_response.getAvailabilityZones()) {
            System.out.printf(
                    "Found availability zone %s " +
                            "with status %s " +
                            "in region %s",
                    zone.getZoneName(),
                    zone.getState(),
                    zone.getRegionName());
            System.out.println();
        }
    }
    public static void listImage(){
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DescribeImagesRequest request = new DescribeImagesRequest();
        request.withOwners("self");

        DescribeImagesResult response = ec2.describeImages(request);

        for(Image image : response.getImages()) {
            System.out.printf("[ImageID] %s, " + "[Name] %s, " + "[Owner] %s ", image.getImageId(), image.getName(), image.getOwnerId());
            System.out.println();
        }
    }
    public static void listInstance(){
        boolean done = false;
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        DescribeInstancesRequest request = new DescribeInstancesRequest();

        while (!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);

            for (Reservation reservation : response.getReservations()) {
                for(Instance instance : reservation.getInstances()) {
                    System.out.printf(
                            "[id] %s, " +
                                    "[AMI] %s, " +
                                    "[type] %s, " +
                                    "[state] %10s, " +
                                    "[monitoring state] %s",
                            instance.getInstanceId(),
                            instance.getImageId(),
                            instance.getInstanceType(),
                            instance.getState().getName(),
                            instance.getMonitoring().getState());
                }
                System.out.println();
            }

            request.setNextToken(response.getNextToken());

            if (response.getNextToken() == null) {
                done = true;
            }
        }
    }
}
