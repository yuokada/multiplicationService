namespace java io.github.yuokada

typedef i32 int

service MultiplicationService
{
	int multiply(1:int n1, 2:int n2),
	int plusOne(1: int n1, 2:int n2),
}
