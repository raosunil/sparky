//
//  ViewController.m
//  Mayo-ipad
//
//  Created by Rishabh Srivastava on 08/10/14.
//  Copyright (c) 2014 rishabh srivastava. All rights reserved.
//

#import "ViewController.h"



@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.view setBackgroundColor:[UIColor clearColor]];
    // Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}




- (IBAction)login:(id)sender {
    
    [self loginToServerWithUser:self.userName.text passwd:self.passWord.text];

    if(userMap != nil && [userMap objectForKey:@"securityToken"] != nil) {
        
        [self performSegueWithIdentifier:@"login_success" sender:self];
    }
    
}

- (void) alertStatus:(NSString *)msg :(NSString *)title :(int) tag
{
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:title
                                                        message:msg
                                                       delegate:self
                                              cancelButtonTitle:@"Ok"
                                              otherButtonTitles:nil, nil];
    alertView.tag = tag;
    [alertView show];
    
}

- (IBAction)backgroundTap:(id)sender {
    [self.view endEditing:YES];
}

-(BOOL) textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
    
}

- (void)loginToServerWithUser:(NSString *)user
                                 passwd:(NSString *)password{
    if (user!=nil && ![user isEqualToString:@""] && password!=nil && ![password isEqualToString:@""]) {
        
    
        NSMutableString *serverUrl = [[NSMutableString alloc] initWithString:mayoWSCommonURL];  // the server
        NSMutableString *urlParam = [[NSMutableString alloc] initWithString:@"service="];
        [urlParam appendString:mayoWSLoginMethodName];
        [urlParam appendString: @"&username="];
        [urlParam appendString:user];
        [urlParam appendString: @"&password="];
        [urlParam appendString:password];
        
        NSString *urlParamEncoded = [urlParam stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        [serverUrl appendString:urlParamEncoded];
        
        NSURL *url = [[NSURL alloc] initWithString:serverUrl];
        
        NSLog(@"%@", serverUrl);
        NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url
                                                               cachePolicy: NSURLRequestReloadIgnoringCacheData timeoutInterval:30.0];
        NSError *error = nil;
        NSURLResponse *response = nil;
        @try {
            NSData *data = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
            if(userMap == nil)
                userMap = [[NSMutableDictionary alloc] init];
            [userMap setDictionary:[NSJSONSerialization JSONObjectWithData:data options:0 error:NULL]];

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
        
    } else {
        [[[UIAlertView alloc] initWithTitle:@"Sorry"
                                    message:@"Enter the username and password to login"
                                   delegate: nil
                          cancelButtonTitle:@"Cancel"
                          otherButtonTitles:nil
          ] show];
    }
    
    
}


@end
