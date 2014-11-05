//
//  ViewController2.h
//  Mayo-ipad
//
//  Created by Nagesh Kumar Uba on 08/10/14.
//  Copyright (c) 2014 Nagesh Kumar Uba. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AssetsLibrary/AssetsLibrary.h>
#import "ViewController.h"


@interface TableViewController : UITableViewController {
}
@property (nonatomic, strong) NSArray *Images;
@property (nonatomic, strong) NSArray *Title;
@property (nonatomic, strong) NSArray *Description;
@end
