//
//  PatientIDViewController.m
//  Mayo-ipad
//
//  Created by Rishabh Srivastava on 14/11/14.
//  Copyright (c) 2014 rishabh srivastava. All rights reserved.
//

#import "PatientIDViewController.h"

@interface PatientIDViewController ()

@end

@implementation PatientIDViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self.view setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"mayobg.png"]]];
    CALayer *btnLayer = [nextToLanding layer];
    [btnLayer setMasksToBounds:YES];
    [btnLayer setCornerRadius:10.0f];
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

-(NSUInteger)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskLandscape;
}

- (IBAction)nextToLanding:(id)sender {
    
    if(self.patientId == nil || [self.patientId.text isEqualToString:@""]) {
        
        [[[UIAlertView alloc] initWithTitle:@"Sorry"
                                    message:@"Enter the patientID and then continue"
                                   delegate: nil
                          cancelButtonTitle:@"Cancel"
                          otherButtonTitles:nil
          ] show];
    }
    
    NSMutableString *serverUrl = [[NSMutableString alloc] initWithString:mayoWSCommonURL];  // the server
    NSMutableString *urlParam = [[NSMutableString alloc] initWithString:@"service="];
    [urlParam appendString:mayoWSLoginMethodName];
    [urlParam appendString: @"&username="];
    [urlParam appendString:self.patientId.text];
    [urlParam appendString: @"&password="];
    [urlParam appendString:@""];
    
    NSString *urlParamEncoded = [urlParam stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [serverUrl appendString:urlParamEncoded];
    
    NSURL *url = [[NSURL alloc] initWithString:serverUrl];
    
    NSLog(@"%@", serverUrl);
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url
                                                           cachePolicy: NSURLRequestReloadIgnoringCacheData timeoutInterval:10.0];
    NSError *error = nil;
    NSURLResponse *response = nil;
    @try {
        NSData *data = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
        if(patientMap == nil)
            patientMap = [[NSMutableDictionary alloc] init];
        [patientMap setDictionary:[NSJSONSerialization JSONObjectWithData:data options:0 error:NULL]];
        
    }
    @catch (NSException *exception) {
        NSLog(@"Reason for Exception is : %@",[exception reason]);
        [[[UIAlertView alloc] initWithTitle:@"Sorry"
                                    message:@"Please ensure VPN is connected and credentials are correct before login"
                                   delegate: nil
                          cancelButtonTitle:@"Cancel"
                          otherButtonTitles:nil
          ] show];
    }
    
    if ([patientMap objectForKey:@"lanId"] == nil){
        [[[UIAlertView alloc] initWithTitle:@"Sorry"
                                    message:@"PatientID does not exist"
                                   delegate: nil
                          cancelButtonTitle:@"Cancel"
                          otherButtonTitles:nil
          ] show];
    }
    
    else{
        
        [self performSegueWithIdentifier:@"patientID_library" sender:self];
        
        //[self performSegueWithIdentifier:@"login_failed" sender:self];
    }

    
   /*
    if(self.patientId == nil || [self.patientId.text isEqualToString:@""])
    {
        [self showErrorAlert];
    }
    
    if(self.patientId == nil || [self.patientId.text isEqualToString:@""])
    {
        [self showErrorAlert];
    }
    */
    
}
-(void) showErrorAlert
{
    UIAlertView *ErrorAlert = [[UIAlertView alloc] initWithTitle:@""
                                                         message:@"Enter the Patient ID and then continue." delegate:nil
                                               cancelButtonTitle:@"OK"
                                               otherButtonTitles:nil, nil];
    [ErrorAlert show];
}

- (IBAction)backgroundTap:(id)sender {
    [self.view endEditing:YES];
}

- (IBAction)LogoutButton:(id)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}


@end
