//
//  uploadViewController.m
//  Mayo-ipad
//
//  Created by Sachin Dheeraj on 11/13/14.
//  Copyright (c) 2014 rishabh srivastava. All rights reserved.
//

#import "uploadViewController.h"

@interface uploadViewController ()

@end

@implementation uploadViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.image.image = self.landingImage;
    // Do any additional setup after loading the view.
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

- (IBAction)upload:(id)sender {
    if(userMap != nil && [userMap objectForKey:@"securityToken"] != nil && self.landingImage != nil && patientMap != nil && [patientMap objectForKey:@"lanId"] != nil) {
        [self->activityView startAnimating];
        activityLabel.text = @"Uploading...";
        [self performSelector:@selector(Afterloading) withObject:nil afterDelay:4.0f];
        
        NSMutableString *serverUrl = [[NSMutableString alloc] initWithString:mayoWSUploadURL];  // the server
        //1416004196564.M137121.Rao.image.iphone
        NSString *timestamp = [NSString stringWithFormat:@"%d",[[NSDate date] timeIntervalSince1970] * 1000];
        NSMutableString *urlParam = [[NSMutableString alloc] initWithString:@"service="];
        [urlParam appendString:@"importFiles"];
        [urlParam appendString: @"&imageLocation="];
        [urlParam appendString:timestamp];
        [urlParam appendString:@"."];
        [urlParam appendString:[patientMap objectForKey:@"lanId"]];
        [urlParam appendString:@"."];
        [urlParam appendString:[patientMap objectForKey:@"lastName"]];
        [urlParam appendString:@".image.iphone"];
        [urlParam appendString: @"&lanid="];
        [urlParam appendString:[userMap objectForKey:@"lanId"]];
        [urlParam appendString: @"&token="];
        [urlParam appendString:[userMap objectForKey:@"securityToken"]];
        [urlParam appendString: @"&count=1"];
        
        NSString *urlParamEncoded = [urlParam stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        [serverUrl appendString:urlParamEncoded];
        
        NSURL *url = [[NSURL alloc] initWithString:serverUrl];
        
        NSData *imageData = UIImageJPEGRepresentation(self.landingImage, 1.0);
        NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];        
        [request setCachePolicy:NSURLRequestReloadIgnoringLocalCacheData];
        [request setHTTPShouldHandleCookies:NO];
        [request setTimeoutInterval:60];
        [request setHTTPMethod:@"POST"];
        
        NSString *boundary = @"unique-consistent-string";
        
        // set Content-Type in HTTP header
        NSString *contentType = [NSString stringWithFormat:@"multipart/form-data; boundary=%@", boundary];
        [request setValue:contentType forHTTPHeaderField: @"Content-Type"];
        
        // post body
        NSMutableData *body = [NSMutableData data];
        
        // add params (all params are strings)
        [body appendData:[[NSString stringWithFormat:@"--%@\r\n", boundary] dataUsingEncoding:NSUTF8StringEncoding]];
        [body appendData:[[NSString stringWithFormat:@"Content-Disposition: form-data; name=%@\r\n\r\n", @"imageCaption"] dataUsingEncoding:NSUTF8StringEncoding]];
        [body appendData:[[NSString stringWithFormat:@"%@\r\n", @"Some Caption"] dataUsingEncoding:NSUTF8StringEncoding]];
        
        // add image data
        if (imageData) {
            [body appendData:[[NSString stringWithFormat:@"--%@\r\n", boundary] dataUsingEncoding:NSUTF8StringEncoding]];
            [body appendData:[[NSString stringWithFormat:@"Content-Disposition: form-data; name=%@; filename=sample.jpg\r\n", @"imageFormKey"] dataUsingEncoding:NSUTF8StringEncoding]];
            [body appendData:[@"Content-Type: image/jpeg\r\n\r\n" dataUsingEncoding:NSUTF8StringEncoding]];
            [body appendData:imageData];
            [body appendData:[[NSString stringWithFormat:@"\r\n"] dataUsingEncoding:NSUTF8StringEncoding]];
        }
        
        [body appendData:[[NSString stringWithFormat:@"--%@--\r\n", boundary] dataUsingEncoding:NSUTF8StringEncoding]];
        
        // setting the body of the post to the reqeust
        [request setHTTPBody:body];
        
        // set the content-length
        NSString *postLength = [NSString stringWithFormat:@"%lu", (unsigned long)[body length]];
        [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
        
        
        [NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue currentQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
            if(data.length > 0)
            {
                //success
                NSMutableDictionary *retMap = [[NSMutableDictionary alloc] init];
                [retMap setDictionary:[NSJSONSerialization JSONObjectWithData:data options:0 error:NULL]];
            }
        }];
        
        NSLog(@"%@", serverUrl);
        
    }else{
        
        [[[UIAlertView alloc] initWithTitle:@"Sorry"
                                    message:@"Please select an image to upload, click 'Cancel Upload' and try again!"
                                   delegate: nil
                          cancelButtonTitle:@"OK"
                          otherButtonTitles:nil
          ] show];
    }
    
    
    
}

-(void)Afterloading{
    activityView.hidden = YES;
    activityLabel.text = @"Image was successfully uploaded!";
    
}
- (IBAction)email:(id)sender {
}


@end
