###########################
# load required libraries #
###########################
library(ggplot2)
library(scales)

##################
# SLO violations #
##################
SLO <- data.frame()
SLO[1, '/'] <- 3000
SLO[1, '/best-sellers'] <- 5000
SLO[1, '/new-products'] <- 5000
SLO[1, '/product-detail'] <- 3000
SLO[1, '/search?searchField=&keyword=&C_ID='] <- 10000
SLO[1, '/search'] <- 3000
SLO[1, '/shopping-cart'] <-  3000
SLO[1, '/customer-registration'] <- 3000
SLO[1, '/buy-confirm'] <- 5000
SLO[1, '/buy'] <- 3000
SLO[1, '/order-inquiry'] <- 3000
SLO[1, '/admin-confirm'] <- 5000
SLO[1, '/admin'] <- 3000
SLO[1, '/payment'] <- 10000

#################################
# read csv files to data frames #
#################################
df<-read.csv(f,header=TRUE)
slo_df<-read.csv(slo_f, header=TRUE)

if(file.exists(as_f))
{
as_df<-read.csv(as_f, header=TRUE)
}

mdf<-read.csv(m_f, header=TRUE, row.names=NULL)
slo_df_non_agg <- read.csv(slo_f_non_aggregated, header=TRUE)
slo_agg_1second_df <- read.csv(slo_agg_1second, header=TRUE)
slo_agg_5seconds_df <- read.csv(slo_agg_5seconds, header=TRUE)
slo_agg_10seconds_df <- read.csv(slo_agg_10seconds, header=TRUE)
ec2_cpu_df <- read.csv(ec2_file, header=TRUE)
rds_cpu_df <- read.csv(rds_cpu_file, header=TRUE)

####################
# define functions #
####################
get_vline <- function(df)
{
    index <- which(is.character(df$violates) & df$violates != "" & !is.na(df$violates))
    return(as.numeric(df[index+1, 'date']))
}

normalized_response_time <- function(df, scale=1)
{
	if( nrow(df) == 0)
	{
		df[1, 'response_time_normalized'] <- 0
		df <- df[-c(1), ]
		return(df)
	}
	my_df <- df
	for(i in 1:nrow(df))
	{
		normalized_value <- scale/SLO[1, df[i, 'url']]
		my_df[i, 'response_time_normalized'] <- df[i, 'response_time']*normalized_value
	}

	return(my_df)
}

cut_scenario <- function(df, duration)
{
	steps <- (scenario_duration_in_min*60)/duration
    if (nrow(df) > steps+1)
    {

        c <- seq.int(nrow(df) + (steps - nrow(df)) + 2, nrow(df), 1)
        return(df[-c,])
    }
    return(df)
}

when_violates <- function(df, start=1)
{
	stop <- FALSE
	for(i in start:nrow(df))
	{
		if(df[i, 'num_threads'] > 10 & !stop)
		{
			df[i-1, 'violates'] <- sprintf("req. = %s (%s) / VU = (%s)", round(df[i-1, "num_requests_theory"]), df[i-1, 'num_all_requests'], round(as.numeric(df[i-1, 'vus'])))
			stop <- TRUE
		}
		else
		{
			df[i, 'violates'] <- ""
		}
	}

	return(df)
}

transform_date <- function(df, field="date")
{
	df[,field] <- as.POSIXct(df[,field]/1000, origin='1970-01-01')
	return(df)
}

order_by_date <- function(df, field="date"){
	my_df<-df[order(df[,field]),]
	return(my_df)
}


create_vus <- function(df)
{
	df<-order_by_date(df)
	threads_per_minute <- num_threads/ (nrow(df)-1)
	for(i in 1:nrow(df)){
		df[i, "vus"] <- round((i-1)*threads_per_minute)
	}
	return(df)
}

add_scale_x <- function(gg, df){
	my_breaks <- seq.int(0, scenario_duration*60, 60)
	return(gg + scale_x_continuous(breaks=my_breaks, labels=format(as.POSIXct(my_breaks, origin="1970-01-01"), format="%M:%S")))
}


date2scenario_time <- function(df, field="date")
{
	min_d <- as.numeric(min(df[,field]))
	df$scenario_date <- as.POSIXct(as.numeric(df[,field])-min_d, origin="1970-01-01")
	return(df)
}

add_requests_per_second <- function(df, duration){
	my_df <- df

	scenario_duration_in_sec <- scenario_duration*60

	requests_per_second <- (num_threads/7)
	requests_per_scenario <- requests_per_second * scenario_duration_in_sec
	requests_per_duration <- requests_per_scenario/(scenario_duration_in_sec/duration)
	inc <- requests_per_duration/nrow(my_df)

    my_df[1, "drek"] <- 0
	for(i in 2:nrow(my_df)){
		my_df[i, "drek"] <- as.numeric((i-1)*inc)
	}
	return(my_df)
}

add_theorethical_requests <- function(df, duration)
{
	scenario_duration_in_sec <- scenario_duration*60
	requests_per_second <- (num_threads/7)
	num_intervals <- scenario_duration_in_sec/duration

	requests_per_scenario <- requests_per_second * scenario_duration_in_sec
	requests_per_duration <- requests_per_scenario/num_intervals

	requests_per_interval <- requests_per_duration/num_intervals
	df[1, "num_requests_theory"] <- 0
	for(i in 1:(nrow(df)-1))
	{
		df[i+1, "num_requests_theory"] <- (((i-1) * requests_per_interval) + (i * requests_per_interval))/2
	}

	return(df)
}

insertrow <- function(existingdf, newrow, r)
{
	existingdf[seq(r+1,nrow(existingdf)+1),] <- existingdf[seq(r,nrow(existingdf)),]
	existingdf[r,] <- newrow
	existingdf
}

multiplot <- function(..., plotlist=NULL, file, cols=1, layout=NULL) {
  require(grid)

  # Make a list from the ... arguments and plotlist
  plots <- c(list(...), plotlist)

  numPlots = length(plots)

  # If layout is NULL, then use 'cols' to determine layout
  if (is.null(layout)) {
    # Make the panel
    # ncol: Number of columns of plots
    # nrow: Number of rows needed, calculated from # of cols
    layout <- matrix(seq(1, cols * ceiling(numPlots/cols)),
                    ncol = cols, nrow = ceiling(numPlots/cols))
  }

 if (numPlots==1) {
    print(plots[[1]])

  } else {
    # Set up the page
    grid.newpage()
    pushViewport(viewport(layout = grid.layout(nrow(layout), ncol(layout))))

    # Make each plot, in the correct location
    for (i in 1:numPlots) {
      # Get the i,j matrix positions of the regions that contain this subplot
      matchidx <- as.data.frame(which(layout == i, arr.ind = TRUE))

      print(plots[[i]], vp = viewport(layout.pos.row = matchidx$row,
                                      layout.pos.col = matchidx$col))
    }
  }
}

########################################
# transform timestamps to date objects #
########################################
slo_df <- transform_date(slo_df)
mdf <- transform_date(mdf)
df <- transform_date(df)
slo_df_non_agg <- transform_date(slo_df_non_agg)
slo_agg_1second_df <- transform_date(slo_agg_1second_df)
slo_agg_5seconds_df <- transform_date(slo_agg_5seconds_df)
slo_agg_10seconds_df <- transform_date(slo_agg_10seconds_df)

############################
# order data frame by date #
############################
slo_df<-order_by_date(slo_df)
mdf <- order_by_date(mdf)
df <- order_by_date(df)
slo_df_non_agg <- order_by_date(slo_df_non_agg)
slo_agg_1second_df <- order_by_date(slo_agg_1second_df)
slo_agg_5seconds_df <- order_by_date(slo_agg_5seconds_df)
slo_agg_10seconds_df <- order_by_date(slo_agg_10seconds_df)
rds_cpu_df <- order_by_date(rds_cpu_df, "timestamp")

################
# Cut scenario #
################
slo_df <- cut_scenario(slo_df, 60)

slo_agg_1second_df <- cut_scenario(slo_agg_1second_df, 1)

slo_agg_5seconds_df <- cut_scenario(slo_agg_5seconds_df, 5)

slo_agg_10seconds_df <- cut_scenario(slo_agg_10seconds_df, 10)


##################
# transform data #
##################
slo_df_non_agg$response_code <- factor(slo_df_non_agg$response_code)

scenario_duration <- c(max(slo_df$date) - min(slo_df$date))

num_ec2_instances <- length(levels(ec2_cpu_df$instance_id))

slo_df <- create_vus(slo_df)
slo_agg_1second_df <- create_vus(slo_agg_1second_df)
slo_agg_5seconds_df <- create_vus(slo_agg_5seconds_df)
slo_agg_10seconds_df <- create_vus(slo_agg_10seconds_df)

specify_decimal <- function(x, k) format(round(x, k), nsmall=k)

# change time to match scenario time
slo_df <- add_requests_per_second(slo_df, 60)
slo_agg_1second_df <- add_requests_per_second(slo_agg_1second_df, 1)
slo_agg_5seconds_df <- add_requests_per_second(slo_agg_5seconds_df, 5)
slo_agg_10seconds_df <- add_requests_per_second(slo_agg_10seconds_df, 10)

ec2_cpu_avg <- aggregate(average ~ timestamp, ec2_cpu_df, mean)
ec2_cpu_avg$timestamp <- seq.int(60, nrow(ec2_cpu_avg)*60, 60)
ec2_cpu_avg <- insertrow(ec2_cpu_avg, c(0,0), 1)

rds_cpu_df <- insertrow(rds_cpu_df, c(as.character(rds_cpu_df[1,"instance_id"]),0,0), 1)
rds_cpu_df$timestamp <- seq.int(0, (nrow(rds_cpu_df)-1)*60, 60)

#############################################
# calculate theorethical number of requests #
#############################################

slo_df <- add_theorethical_requests(slo_df, 60)
slo_agg_1second_df <- add_theorethical_requests(slo_agg_1second_df, 1)
slo_agg_5seconds_df <- add_theorethical_requests(slo_agg_5seconds_df, 5)
slo_agg_10seconds_df <- add_theorethical_requests(slo_agg_10seconds_df, 10)

##########################################
# calculate percentage of slo violations #
##########################################

slo_df$num_threads <- ifelse(slo_df$num > 0, specify_decimal((100*slo_df$num)/slo_df$num_all_requests, 2), "")
slo_agg_5seconds_df$num_threads <- ifelse(slo_agg_5seconds_df$num > 0, specify_decimal((100*slo_agg_5seconds_df$num)/slo_agg_5seconds_df$num_all_requests, 2), "")
slo_agg_10seconds_df$num_threads <- ifelse(slo_agg_10seconds_df$num > 0, specify_decimal((100*slo_agg_10seconds_df$num)/slo_agg_10seconds_df$num_all_requests, 2), "")

##################################
# add text when starts violating #
##################################

slo_df <- when_violates(slo_df)
slo_agg_5seconds_df <- when_violates(slo_agg_5seconds_df, start=10)
slo_agg_10seconds_df <- when_violates(slo_agg_10seconds_df, start=5)

####################################
# transform times to scenario time #
####################################
slo_df <- date2scenario_time(slo_df)

slo_agg_1second_df <- date2scenario_time(slo_agg_1second_df)

slo_agg_5seconds_df <- date2scenario_time(slo_agg_5seconds_df)

slo_agg_10seconds_df <-date2scenario_time(slo_agg_10seconds_df)

df <- date2scenario_time(df)

slo_df_non_agg <- date2scenario_time(slo_df_non_agg)

mdf <- date2scenario_time(mdf)

slo_df_non_agg <- normalized_response_time(slo_df_non_agg)

#################
# define graphs #
#################
common_1minute_gg <- ggplot(slo_df, aes(x=as.numeric(scenario_date), y=num_all_requests)) +
	geom_line() +
	geom_vline(xintercept=get_vline(slo_df), colour="red") +
	geom_line(data=slo_df, aes(x=as.numeric(scenario_date), y=drek)) +
	geom_bar(stat="identity", data=slo_df, aes(x=as.numeric(scenario_date), y=num)) +
	geom_text(data=slo_df, size=5, vjust=-1.5, aes(label=violates))

common_5seconds_gg <- ggplot(slo_agg_5seconds_df, aes(x=as.numeric(scenario_date), y=num_all_requests)) +
	geom_line() +
	geom_vline(xintercept=get_vline(slo_agg_5seconds_df), colour="red") +
	geom_line(data=slo_agg_5seconds_df, aes(x=as.numeric(scenario_date), y=drek)) +
	geom_bar(stat="identity", data=slo_agg_5seconds_df, aes(x=as.numeric(scenario_date), y=num)) +
	geom_text(data=slo_agg_5seconds_df, size=5, vjust=-1.5, aes(label=violates))

common_10seconds_gg <- ggplot(slo_agg_10seconds_df, aes(x=as.numeric(scenario_date), y=num_all_requests)) +
	geom_line() +
	geom_vline(xintercept=get_vline(slo_agg_10seconds_df), colour="red") +
	geom_line(data=slo_agg_10seconds_df, aes(x=as.numeric(scenario_date), y=drek)) +
	geom_bar(stat="identity", data=slo_agg_10seconds_df, aes(x=as.numeric(scenario_date), y=num)) +
	geom_text(data=slo_agg_10seconds_df, size=5, vjust=-1.5, aes(label=violates))

scenario_gg <- ggplot(slo_df, aes(x=as.numeric(scenario_date), y=num_all_requests)) +
	geom_line(stat="identity") +
	ylab(label="no requests") +
	xlab(label="time")

slo_gg2 <- ggplot(slo_df, aes(x=as.numeric(scenario_date), y=num)) +
	geom_bar(stat="identity") +
	ylab(label="no of slo violations") +
	xlab(label="time")

slo_non_agg_gg <- ggplot(slo_df_non_agg, aes(x=as.numeric(scenario_date), y=response_time, colour=response_code)) +
	geom_point() +
	ylab(label="response time") +
	xlab(label="time")

slo_non_agg_gg_urls <- ggplot(slo_df_non_agg, aes(x=as.numeric(scenario_date), y=response_time, colour=url)) +
	geom_point() +
	ylab(label="response time") +
	xlab(label="time")

slo_non_agg_gg_urls_normalized <- ggplot(slo_df_non_agg, aes(x=as.numeric(scenario_date), y=response_time_normalized, colour=url)) +
	geom_point() +
	ylab(label="response time") +
	xlab(label="time") +
	ggtitle("slo violations by url - normalized")


slo_non_agg_gg_normalized <- ggplot(slo_df_non_agg, aes(x=as.numeric(scenario_date), y=response_time_normalized, colour=response_code)) +
	geom_point() +
	ylab(label="response time") +
	xlab(label="time") +
	ggtitle("slo_violations by response code - normalized")

gg <- ggplot(df, aes(x=as.numeric(scenario_date), y=response_time, colour=url)) +
	geom_point() +
	xlab(label="time")

gg2 <- ggplot(slo_df, aes(x=vus, y=num_all_requests)) +
	geom_point(stat="identity") +
	scale_x_continuous(breaks=seq(0, max(slo_df$vus), num_threads/10))
	#geom_line(data=slo_df, aes(x=as.numeric(scenario_date), y=drek)) +

slo_agg_1second_gg <- ggplot(slo_agg_1second_df, aes(x=as.numeric(scenario_date), y=num_all_requests)) +
	geom_line(stat="identity") +
	ylab(label="no requests") +
	xlab(label="time")

slo_agg_5seconds_gg <- ggplot(slo_agg_5seconds_df, aes(x=as.numeric(scenario_date), y=num_all_requests)) +
	geom_line(stat="identity") +
	ylab(label="no requests") +
	xlab(label="time")

slo_agg_10seconds_gg <- ggplot(slo_agg_10seconds_df, aes(x=as.numeric(scenario_date), y=num_all_requests)) +
	geom_line(stat="identity") +
	ylab(label="no requests") +
	xlab(label="time")

ec2_cpu_gg <- ggplot(ec2_cpu_avg, aes(x=as.numeric(timestamp), y=average)) +
	geom_line() +
	geom_point() +
	ylab("avg. cpu utilization") +
	xlab("time") +
	geom_text(vjust=2, aes(label=round(as.numeric(average),digits=2)))

rds_cpu_gg <- ggplot(rds_cpu_df, aes(x=as.numeric(timestamp), y=as.double(average))) +
	geom_line() +
	geom_point() +
	ylab("avg. cpu utilization") +
	xlab("time") +
	geom_text(vjust=2, aes(label=round(as.numeric(average), digits=2)))


##################
# scale x-origin #
##################

common_1minute_gg <- add_scale_x(common_1minute_gg, slo_df)

common_5seconds_gg <- add_scale_x(common_5seconds_gg, slo_agg_5seconds_df)

common_10seconds_gg <- add_scale_x(common_10seconds_gg, slo_agg_10seconds_df)

scenario_gg <- add_scale_x(scenario_gg, slo_df)

slo_gg2 <- add_scale_x(slo_gg2, slo_df)

slo_non_agg_gg <- add_scale_x(slo_non_agg_gg, slo_df_non_agg)

slo_non_agg_gg_urls <- add_scale_x(slo_non_agg_gg_urls, slo_df_non_agg)

slo_non_agg_gg_urls_normalized <- add_scale_x(slo_non_agg_gg_urls_normalized, slo_df_non_agg)

slo_non_agg_gg_normalized <- add_scale_x(slo_non_agg_gg_normalized, slo_df_non_agg)

gg <- add_scale_x(gg, df)

slo_agg_1second_gg <- add_scale_x(slo_agg_1second_gg, slo_agg_1second_df)

slo_agg_5seconds_gg <- add_scale_x(slo_agg_5seconds_gg,slo_agg_5seconds_df)

slo_agg_10seconds_gg <- add_scale_x(slo_agg_10seconds_gg,slo_agg_10seconds_df)

ec2_cpu_gg <- add_scale_x(ec2_cpu_gg, ec_cpu_avg)

ec2_cpu_gg <- ec2_cpu_gg + scale_y_continuous(breaks=seq.int(0, 100, 10))

rds_cpu_gg <- add_scale_x(rds_cpu_gg, rds_cpu_df)

rds_cpu_gg <- rds_cpu_gg + scale_y_continuous(breaks=seq.int(0, 100, 10))

max_date <- max(slo_df$date)

if(exists("as_df"))
{
filtered_as <- as_df[as.numeric(as.POSIXct(as_df$end_time)) < as.numeric(max_date),]
}

########################
# add layers to graphs #
########################

common_1minute_gg <- common_1minute_gg + xlab(label='time') + ylab(label='requests') + ggtitle("slo violations - 1 minute")

common_5seconds_gg <- common_5seconds_gg + xlab(label='time') + ylab(label='requests') + ggtitle("slo violations - 5 second")

common_10seconds_gg <- common_10seconds_gg + xlab(label='time') + ylab(label='requests') + ggtitle("slo violations - 10 seconds")

scenario_gg <- scenario_gg + geom_line(data=mdf, aes(x=date,y=y*1000, colour=instance_id), size=2) + ggtitle("requests aggregated by 1 minute")

slo_agg_1second_gg <- slo_agg_1second_gg + geom_line(data=mdf, aes(x=date,y=y*1000, colour=instance_id), size=2) + ggtitle("requests aggregated by 1 second")

slo_agg_5seconds_gg <- slo_agg_5seconds_gg + geom_line(data=mdf, aes(x=date,y=y*1000, colour=instance_id), size=2) + ggtitle("requests aggregated by 5 seconds")

slo_agg_10seconds_gg <- slo_agg_10seconds_gg + geom_line(data=mdf, aes(x=date,y=y*1000, colour=instance_id), size=2) + ggtitle("requests aggregated by 10 seconds")

my_gg <- slo_agg_10seconds_gg + geom_line(data=slo_df, aes(x=as.numeric(scenario_date), y=vus))

gg2 <- gg2 + xlab(label='virtual users') + ylab(label='requests')

slo_non_agg_gg_urls <- slo_non_agg_gg_urls + xlab(label='time') + ylab(label='response time') + ggtitle("slo violations by url")

ec2_cpu_gg <- ec2_cpu_gg + ggtitle(paste("average cpu utilization of", num_ec2_instances, "instances - by minute", sep=" "))

rds_cpu_gg <- rds_cpu_gg + ggtitle(paste("average cpu utilization of rds - by minute"))

################################
# add vm provisioning to graph #
################################
if(nrow(mdf) > 0)
{
slo_gg2 <- slo_gg2 + geom_line(data=mdf, aes(x=date,y=y*10, colour=instance_id), size=2)

slo_non_agg_gg <- slo_non_agg_gg + geom_line(data=mdf, aes(x=date,y=y*1000, colour=instance_id), size=2)

gg <- gg + geom_line(data=mdf, aes(x=date,y=y*1000, colour=instance_id), size=2)
}

slo_gg2 <- slo_gg2 + ggtitle("slo violations - 1 minute")

slo_non_agg_gg <- slo_non_agg_gg + ggtitle("slo violations by response code")

gg <- gg + xlab(label='time') + ylab(label='response time') + ggtitle("all responses")

min_y <- ifelse(nrow(mdf) > 0, min(mdf$y), 0)
slo_gg2 <- slo_gg2 +
	geom_text(data=slo_df, size=3, vjust=-0.5, aes(label=num_threads)) +
	ylim(min_y * 10, max(slo_df$num) + 50)
	# xlim(min(df$date), max(df$date))

#######################
# save graphs to file #
#######################
png(output_file, width=2000, height=6000, res=100)
multiplot(
ec2_cpu_gg,
rds_cpu_gg,
slo_gg2,
	slo_non_agg_gg,
	slo_non_agg_gg_urls,
	slo_non_agg_gg_urls_normalized,
	slo_non_agg_gg_normalized,
	gg,
#	scenario_gg,
#	slo_agg_1second_gg,
#	slo_agg_5seconds_gg,
#	slo_agg_10seconds_gg,
#	my_gg,
	gg2,
	common_1minute_gg,
	common_5seconds_gg,
	common_10seconds_gg)
